package org.violetmoon.zetaimplforge;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.bus.ZetaEventBus;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.ModuleFinder;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zeta.util.handler.LoaderSpecificEventsHandler;
import org.violetmoon.zetaimplforge.block.IForgeBlockBlockExtensions;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.config.ForgeBackedConfig;
import org.violetmoon.zetaimplforge.event.ForgeZetaEventBus;
import org.violetmoon.zetaimplforge.event.load.ForgeZRegister;
import org.violetmoon.zetaimplforge.item.IForgeItemItemExtensions;
import org.violetmoon.zetaimplforge.network.ForgeZetaNetworkHandler;
import org.violetmoon.zetaimplforge.registry.ForgeBrewingRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeZetaRegistry;
import org.violetmoon.zetaimplforge.util.ForgeCreativeTabHandler;
import org.violetmoon.zetaimplforge.util.ForgeLoaderSpecificEventsHandler;
import org.violetmoon.zetaimplforge.util.ForgeRaytracingUtil;

import java.util.List;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {

    private boolean firstRegEvent = false;

    public ForgeZeta(String modid, Logger log, Object config,
                     ModuleFinder finder, List<ZetaCategory> categories, int networkProtocol) {
        super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()), FMLEnvironment.production,
                config, finder, categories, networkProtocol);

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        //hook up config events
        ConfigEventDispatcher configEventDispatcher = new ConfigEventDispatcher(this);
        modbus.addListener(configEventDispatcher::modConfigReloading);
        modbus.addListener(configEventDispatcher::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(configEventDispatcher::serverAboutToStart);

        //other stuff
        modbus.addListener(EventPriority.HIGHEST, this::onSoundsRegistering);
    }

    @Override
    protected ZetaEventBus<IZetaLoadEvent> createLoadBus() {
        //return new StandaloneZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log);
        return ForgeZetaEventBus.ofLoadBus(log, this);
    }

    @Override
    protected ForgeZetaEventBus<IZetaPlayEvent, ?> createPlayBus() {
        return ForgeZetaEventBus.ofPlayBus(log, this);
    }

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public @Nullable String getModDisplayName(String modid) {
        return ModList.get().getModContainerById(modid)
                .map(c -> c.getModInfo().getDisplayName())
                .orElse(null);
    }

    @Override
    protected IZetaConfigInternals createConfigInternals(SectionDefinition rootSection) {
        var forgeConfigWrapper = new ForgeBackedConfig(rootSection);
        this.asZeta().log.info("Early loading configs for " + this.modid);
        forgeConfigWrapper.hackilyLoadEarly();
        return forgeConfigWrapper;
    }

    @Override
    protected ZetaRegistry createRegistry() {
        return new ForgeZetaRegistry(this);
    }

    @Override
    protected LoaderSpecificEventsHandler createLoaderEventsHandler() {
        return new ForgeLoaderSpecificEventsHandler();
    }

    @Override
    protected CreativeTabHandler createCreativeTabHandler() {
        return new ForgeCreativeTabHandler();
    }

    @Override
    protected CraftingExtensionsRegistry createCraftingExtensionsRegistry() {
        return new ForgeCraftingExtensionsRegistry();
    }

    @Override
    protected BrewingRegistry createBrewingRegistry() {
        return new ForgeBrewingRegistry(this);
    }

    @Override
    protected PottedPlantRegistry createPottedPlantRegistry() {
        return (resloc, potted) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(resloc, () -> potted);
    }

    @Override
    protected ZetaCapabilityManager createCapabilityManager() {
        return ForgeCapabilityManager.INSTANCE;
    }

    @Override
    protected BlockExtensionFactory createBlockExtensionFactory() {
        return block -> IForgeBlockBlockExtensions.INSTANCE;
    }

    @Override
    protected ItemExtensionFactory createItemExtensionFactory() {
        return stack -> IForgeItemItemExtensions.INSTANCE;
    }

    @Override
    protected RaytracingUtil createRaytracingUtil() {
        return new ForgeRaytracingUtil();
    }

    @Override
    protected ZetaNetworkHandler createNetworkHandler(int protocolVersion) {
        return new ForgeZetaNetworkHandler(this, protocolVersion);
    }

    //so register event fires one time PER registry!!
    public void onSoundsRegistering(RegisterEvent e) {
        ((ForgeZetaRegistry)registry).onRegisterEvent(e);
    }


    //public void addReloadListener(AddReloadListenerEvent e) {
    //    loadBus.fire(new ForgeZAddReloadListener(e), ZAddReloadListener.class);
    //}

    public static ZResult from(Event.Result r) {
        return switch (r) {
            case DENY -> ZResult.DENY;
            case DEFAULT -> ZResult.DEFAULT;
            case ALLOW -> ZResult.ALLOW;
        };
    }

    public static Event.Result to(ZResult r) {
        return switch (r) {
            case DENY -> Event.Result.DENY;
            case DEFAULT -> Event.Result.DEFAULT;
            case ALLOW -> Event.Result.ALLOW;
        };
    }
}
