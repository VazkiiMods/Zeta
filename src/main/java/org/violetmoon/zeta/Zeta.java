package org.violetmoon.zeta;

import java.util.function.Supplier;

import com.google.common.base.Stopwatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.config.ConfigManager;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.ZetaEventBus;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.module.ModuleFinder;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zeta.module.ZetaModuleManager;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.NameChanger;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.RegistryUtil;
import org.violetmoon.zeta.util.ZetaCommonProxy;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zeta.util.handler.FuelHandler;
import org.violetmoon.zeta.util.zetalist.IZeta;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zeta.world.EntitySpawnHandler;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta implements IZeta {

    public Zeta(String modid, Logger log, ZetaSide side, boolean isProduction) {
        this.log = log;

        this.modid = modid;
        this.side = side;
        this.isProduction = isProduction; //TODO: either have all these constants or static helpers here or in Utils. Not both
        this.proxy = createProxy(side);

        this.modules = createModuleManager();
        this.registry = createRegistry();
        this.renderLayerRegistry = createRenderLayerRegistry();
        this.dyeables = createDyeablesRegistry();
        //this.craftingExtensions = createCraftingExtensionsRegistry();
        this.brewingRegistry = createBrewingRegistry();
        this.advancementModifierRegistry = createAdvancementModifierRegistry();
        this.pottedPlantRegistry = createPottedPlantRegistry();

		this.blockExtensions = createBlockExtensionFactory();
		this.itemExtensions = createItemExtensionFactory();
		//this.capabilityManager = createCapabilityManager();

        this.raytracingUtil = createRaytracingUtil();
        this.nameChanger = createNameChanger();
        this.fuel = createFuelHandler();

        this.entitySpawn = createEntitySpawnHandler();

        Stopwatch stopwatch = Stopwatch.createStarted();
        this.loadBus = this.createLoadBus();
        this.playBus = this.createPlayBus();
        long elapsed = stopwatch.stop().elapsed().toMillis();

        ZetaList.INSTANCE.register(this);
    }

    //core
    public final Logger log;
    public final String modid;
    public final ZetaSide side;
    public final boolean isProduction;
    public final ZetaEventBus<IZetaLoadEvent> loadBus; //zeta specific bus. The "this mod" bus
    // Be careful when using this. Load bus will only fire stuff to this zeta events. Play bus however will not as it delegate to forge bus
    public final ZetaEventBus<IZetaPlayEvent> playBus; //common mod event bus. Each zeta will have their own object for now but internally they all delegate to the same internal bus
    public final ZetaModuleManager modules;
    public final ZetaCommonProxy proxy;

    //registry
    //TODO: make private
    @Deprecated(forRemoval = true)
    public final ZetaRegistry registry;
    public final RegistryUtil registryUtil = new RegistryUtil(this); //TODO: !!Delete this, only needed cause there's no way to get early registry names.
    public final RenderLayerRegistry renderLayerRegistry;
    public final DyeablesRegistry dyeables;
    //public final CraftingExtensionsRegistry craftingExtensions;
    public final BrewingRegistry brewingRegistry;
    public final AdvancementModifierRegistry advancementModifierRegistry;
    public final PottedPlantRegistry pottedPlantRegistry;
    public final VariantRegistry variantRegistry = new VariantRegistry(this);

    //extensions
    public final BlockExtensionFactory blockExtensions;
    public final ItemExtensionFactory itemExtensions;

    //misc :tada:
    public final RaytracingUtil raytracingUtil;
    public final NameChanger nameChanger;
    public final FuelHandler fuel;

    //config (which isn't set in the constructor b/c module loading has to happen first)
    public ConfigManager configManager;
    public IZetaConfigInternals configInternals;

    //network (which isn't set in the constructor b/c it has a user-specified protocol version TODO this isnt good api design, imo)
    //public ZetaNetworkHandler network;

    // worldgen
    public EntitySpawnHandler entitySpawn;

    protected abstract ZetaEventBus<IZetaPlayEvent> createPlayBus();

    protected abstract ZetaEventBus<IZetaLoadEvent> createLoadBus();

    /**
     * @param categories List of module categories in this mod, if null, will not load Modules but still load general config
     * @param finder     Module finder instance to locate the modules this Zeta will load, if null, will not load Modules but still load general config
     * @param rootPojo   General config object root
     */
    // call this in mod init otherwise zeta won't do much
    public final void loadModules(@Nullable Iterable<ZetaCategory> categories, @Nullable ModuleFinder finder, Object rootPojo) {
        if (categories != null && finder != null) {
            modules.initCategories(categories);
            modules.load(finder);
        }

        //The reason why there's a circular dependency between configManager and configInternals:
        // - ConfigManager determines the shape and layout of the config file
        // - The platform-specific configInternals loads the actual values, from the platform-specfic config file
        // - Only then can ConfigManager do the initial config load

        this.configManager = new ConfigManager(this, rootPojo);
        this.configInternals = makeConfigInternals(configManager.getRootConfig());
        asZeta().log.info("Doing super early config setup for {}", asZeta().modid);
        this.configManager.onReload();

        this.modules.doFinalize();
    }

    // modloader services
    public abstract boolean isModLoaded(String modid);

    public abstract @Nullable String getModDisplayName(String modid);

    public <T> T modIntegration(String compatWith, Supplier<Supplier<T>> yes, Supplier<Supplier<T>> no) {
        try {
            return (isModLoaded(compatWith) ? yes : no).get().get();
        } catch (Exception e) {
            throw new RuntimeException("Zeta: " + modid + " threw exception initializing compat with " + compatWith, e);
        }
    }

    // proxy
    public ZetaCommonProxy createProxy(ZetaSide effectiveSide) {
        try {
            if(effectiveSide == ZetaSide.CLIENT)
	            return (ZetaCommonProxy) Class.forName("org.violetmoon.zeta.client.ZetaClientProxy")
                .getConstructor(Zeta.class).newInstance(this);
            else return new ZetaCommonProxy(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct proxy", e);
        }
    }

    // config
    public abstract IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection);

    // general xplat stuff
    public ZetaModuleManager createModuleManager() {
        return new ZetaModuleManager(this);
    }

    public abstract ZetaRegistry createRegistry();

    public RenderLayerRegistry createRenderLayerRegistry() {
        return new RenderLayerRegistry();
    }

    //public abstract CraftingExtensionsRegistry createCraftingExtensionsRegistry();

    public DyeablesRegistry createDyeablesRegistry() {
        return new DyeablesRegistry();
    }

    public abstract BrewingRegistry createBrewingRegistry();

    public AdvancementModifierRegistry createAdvancementModifierRegistry() {
        return new AdvancementModifierRegistry(this);
    }

    public abstract PottedPlantRegistry createPottedPlantRegistry();

    public BlockExtensionFactory createBlockExtensionFactory() {
        return BlockExtensionFactory.DEFAULT;
    }

    public abstract ItemExtensionFactory createItemExtensionFactory();

    public abstract RaytracingUtil createRaytracingUtil();

    public NameChanger createNameChanger() {
        return new NameChanger();
    }

    public FuelHandler createFuelHandler() {
        return new FuelHandler(this);
    }

    public EntitySpawnHandler createEntitySpawnHandler() {
        return new EntitySpawnHandler(this);
    }

    //public abstract ZetaNetworkHandler createNetworkHandler(int protocolVersion);

    // ummmmmm why is this here??
    public abstract boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr);

    //TODO: CAREFULLY evaluate usages of this function, do not use it willy nilly. Sometimes it is necessary though.
    // The name is unwieldy on purpose, usages of this function should stick out.
    public abstract @Nullable RegistryAccess hackilyGetCurrentLevelRegistryAccess();

    // Let's Jump
    public void start(){
        loadBus//.subscribe(craftingExtensions)
                .subscribe(dyeables)
                .subscribe(brewingRegistry)
                .subscribe(fuel)
                .subscribe(entitySpawn);

        playBus.subscribe(fuel)
                .subscribe(advancementModifierRegistry);
    }

    @Override
    public Zeta asZeta() {
        return this;
    }
}
