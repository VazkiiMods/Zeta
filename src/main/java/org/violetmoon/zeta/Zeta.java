package org.violetmoon.zeta;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
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
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.*;
import org.violetmoon.zeta.util.handler.FuelHandler;
import org.violetmoon.zeta.util.handler.LoaderSpecificEventsHandler;
import org.violetmoon.zeta.util.zetalist.IZeta;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zeta.world.EntitySpawnHandler;

import java.util.List;
import java.util.function.Supplier;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta implements IZeta {

    public Zeta(String modid, Logger log, ZetaSide side, boolean isProduction, Object configPojo,
                ModuleFinder finder, List<ZetaCategory> categories, int networkProtocol) {
        this.log = log;

        this.modid = modid;
        this.side = side;
        this.isProduction = isProduction;
        this.proxy = createProxy(side);

        this.modules = createModuleManager();
        this.registry = createRegistry();
        this.renderLayerRegistry = createRenderLayerRegistry();
        this.dyeables = createDyeablesRegistry();
        this.craftingExtensions = createCraftingExtensionsRegistry();
        this.brewingRegistry = createBrewingRegistry();
        this.advancementModifierRegistry = createAdvancementModifierRegistry();
        this.pottedPlantRegistry = createPottedPlantRegistry();

        this.blockExtensions = createBlockExtensionFactory();
        this.itemExtensions = createItemExtensionFactory();
        this.capabilityManager = createCapabilityManager();

        this.raytracingUtil = createRaytracingUtil();
        this.nameChanger = createNameChanger();
        this.fuel = createFuelHandler();

        this.entitySpawn = createEntitySpawnHandler();
        this.loaderSpecificEvents = createLoaderEventsHandler();

        this.loadBus = this.createLoadBus();
        this.playBus = this.createPlayBus();

        this.network = createNetworkHandler(networkProtocol);
        this.creativeTabs = this.createCreativeTabHandler();


        //manage subscriptions
        this.loadBus.subscribe(craftingExtensions)
                .subscribe(dyeables)
                .subscribe(brewingRegistry)
                .subscribe(fuel)
                .subscribe(entitySpawn)
                .subscribe(creativeTabs)
                .subscribe(configPojo).subscribe(configPojo.getClass());

        this.playBus.subscribe(fuel)
                .subscribe(advancementModifierRegistry)
                .subscribe(configPojo).subscribe(configPojo.getClass());

        //load modules

        //things need to happen in order:
        // - load modules
        // - load config
        // - fire config bindings (on reload) so we can set up module enabled stuff inside the modules
        // - subscribe module to bus
        // - fire initial config loaded event
        this.modules.initialize(finder, categories);

        this.configManager = new ConfigManager(this, configPojo, this::createConfigInternals);
        // set initial state for modules based off configs
        this.configManager.onReload();
        // hooks modules up to the now available configs which will immediately be queried to see which module is enabled
        this.modules.setupBusSubscriptions();
        // fire the initial config loaded event which will trigger events for each mod
        this.configManager.onZetaReady();

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
    //we handle registration
    public final ZetaRegistry registry;
    public final RegistryUtil registryUtil = new RegistryUtil(this); //TODO: !!Delete this, only needed cause there's no way to get early registry names.
    public final RenderLayerRegistry renderLayerRegistry;
    public final DyeablesRegistry dyeables;
    public final CraftingExtensionsRegistry craftingExtensions;
    public final BrewingRegistry brewingRegistry;
    public final AdvancementModifierRegistry advancementModifierRegistry;
    public final PottedPlantRegistry pottedPlantRegistry;
    public final VariantRegistry variantRegistry = new VariantRegistry(this);

    //extensions
    public final ZetaCapabilityManager capabilityManager;
    public final BlockExtensionFactory blockExtensions;
    public final ItemExtensionFactory itemExtensions;

    //misc :tada:
    public final RaytracingUtil raytracingUtil;
    public final NameChanger nameChanger;
    public final FuelHandler fuel;
    public final LoaderSpecificEventsHandler loaderSpecificEvents;

    public final ConfigManager configManager;
    public final ZetaNetworkHandler network;
    public final CreativeTabHandler creativeTabs;
    // worldgen
    public final EntitySpawnHandler entitySpawn;

    public ResourceLocation makeId(String name) {
        //You know how `new ResourceLocation(String)` prepends "minecraft" if there's no prefix?
        //This method is like that, except it prepends *your* modid
        if (name.indexOf(':') == -1) return new ResourceLocation(this.modid, name);
        else return new ResourceLocation(name);
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

    public abstract boolean hasCompletedRegistration();

    // proxy madness
    protected ZetaCommonProxy createProxy(ZetaSide effectiveSide) {
        try {
            if (effectiveSide == ZetaSide.CLIENT)
                return (ZetaCommonProxy) Class.forName("org.violetmoon.zeta.client.ZetaClientProxy")
                        .getConstructor(Zeta.class).newInstance(this);
            else return new ZetaCommonProxy(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct proxy", e);
        }
    }

    // Inheritance loader specific stuff

    protected abstract ZetaEventBus<IZetaPlayEvent> createPlayBus();

    protected abstract ZetaEventBus<IZetaLoadEvent> createLoadBus();

    // config
    protected abstract IZetaConfigInternals createConfigInternals(SectionDefinition rootSection);

    // general xplat stuff

    protected abstract ZetaRegistry createRegistry();

    protected abstract CraftingExtensionsRegistry createCraftingExtensionsRegistry();

    protected abstract BrewingRegistry createBrewingRegistry();

    protected abstract PottedPlantRegistry createPottedPlantRegistry();

    protected abstract ZetaCapabilityManager createCapabilityManager();

    protected abstract ItemExtensionFactory createItemExtensionFactory();

    protected abstract RaytracingUtil createRaytracingUtil();

    protected abstract ZetaNetworkHandler createNetworkHandler(int protocolVersion);

    protected abstract LoaderSpecificEventsHandler createLoaderEventsHandler();

    protected abstract CreativeTabHandler createCreativeTabHandler();

    protected ZetaModuleManager createModuleManager() {
        return new ZetaModuleManager(this);
    }

    protected AdvancementModifierRegistry createAdvancementModifierRegistry() {
        return new AdvancementModifierRegistry(this);
    }

    protected DyeablesRegistry createDyeablesRegistry() {
        return new DyeablesRegistry(this);
    }

    protected BlockExtensionFactory createBlockExtensionFactory() {
        return BlockExtensionFactory.DEFAULT;
    }

    protected NameChanger createNameChanger() {
        return new NameChanger();
    }

    protected FuelHandler createFuelHandler() {
        return new FuelHandler(this);
    }

    protected EntitySpawnHandler createEntitySpawnHandler() {
        return new EntitySpawnHandler(this);
    }

    protected RenderLayerRegistry createRenderLayerRegistry() {
        return new RenderLayerRegistry();
    }

    @Override
    public Zeta asZeta() {
        return this;
    }
}
