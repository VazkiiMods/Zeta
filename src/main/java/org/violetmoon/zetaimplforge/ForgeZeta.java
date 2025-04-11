package org.violetmoon.zetaimplforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.*;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.ZLootTableLoad;
import org.violetmoon.zeta.event.play.loading.ZVillagerTrades;
import org.violetmoon.zeta.event.play.loading.ZWandererTrades;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.bus.ZetaEventBus;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.registry.BrewingRegistry;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.registry.PottedPlantRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.block.IForgeBlockBlockExtensions;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.config.ForgeBackedConfig;
import org.violetmoon.zetaimplforge.config.TerribleForgeConfigHackery;
import org.violetmoon.zetaimplforge.event.load.ForgeZAddReloadListener;
import org.violetmoon.zetaimplforge.event.load.ForgeZCommonSetup;
import org.violetmoon.zetaimplforge.event.load.ForgeZEntityAttributeCreation;
import org.violetmoon.zetaimplforge.event.load.ForgeZLoadComplete;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZLootTableLoad;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZVillagerTrades;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZWandererTrades;
import org.violetmoon.zetaimplforge.event.ForgeZetaEventBus;
import org.violetmoon.zetaimplforge.event.load.ForgeZRegister;
import org.violetmoon.zetaimplforge.item.IForgeItemItemExtensions;
import org.violetmoon.zetaimplforge.registry.ForgeBrewingRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeZetaRegistry;
import org.violetmoon.zetaimplforge.util.ForgeRaytracingUtil;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
    public ForgeZeta(String modid, Logger log) {
        super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()), FMLEnvironment.production);
    }

    @Override
    protected ZetaEventBus<IZetaLoadEvent> createLoadBus() {
        //return new StandaloneZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log);
        return ForgeZetaEventBus.ofLoadBus( log, this);
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
	public IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection) {
		ModConfigSpec.Builder bob = new ModConfigSpec.Builder();
		ForgeBackedConfig forge = new ForgeBackedConfig(rootSection, bob);
		ModConfigSpec spec = bob.build();

        TerribleForgeConfigHackery.registerAndLoadConfigEarlierThanUsual(spec);

        return forge;
    }

    @Override
    public ZetaRegistry createRegistry() {
        return new ForgeZetaRegistry(this);
    }

    @Override
    public CraftingExtensionsRegistry createCraftingExtensionsRegistry() {
        return new ForgeCraftingExtensionsRegistry();
    }

    @Override
    public BrewingRegistry createBrewingRegistry() {
        return new ForgeBrewingRegistry(this);
    }

    @Override
    public PottedPlantRegistry createPottedPlantRegistry() {
        return (resloc, potted) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(resloc, () -> potted);
    }

	/*@Override
	public ZetaCapabilityManager createCapabilityManager() {
		return new ForgeCapabilityManager();
	}*/

    @Override
    public BlockExtensionFactory createBlockExtensionFactory() {
        return block -> IForgeBlockBlockExtensions.INSTANCE;
    }

    @Override
    public ItemExtensionFactory createItemExtensionFactory() {
        return stack -> IForgeItemItemExtensions.INSTANCE;
    }

    @Override
    public RaytracingUtil createRaytracingUtil() {
        return new ForgeRaytracingUtil();
    }

    @Override
    public ZetaNetworkHandler createNetworkHandler(int protocolVersion) {
        return new ForgeZetaNetworkHandler(this, protocolVersion);
    }

    @Override
    public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
        return NeoForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
    }

    @SuppressWarnings("duplicates")
    @Override
    public void start() {
        super.start();
        //load
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        //hook up config events
        ConfigEventDispatcher configEventDispatcher = new ConfigEventDispatcher(this);
        modbus.addListener(configEventDispatcher::modConfigReloading);
        modbus.addListener(configEventDispatcher::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(configEventDispatcher::serverAboutToStart);

        //other stuff
        modbus.addListener(EventPriority.LOWEST, CreativeTabManager::buildContents);
        modbus.addListener(EventPriority.HIGHEST, this::registerHighest);
    }

    private boolean registerDone = false;

    public void registerHighest(RegisterEvent e) {
        if (registerDone)
            return;

        registerDone = true; // do this *before* actually registering to prevent weird ??race conditions?? or something?
        //idk whats going on, all i know is i started the game, got a log with 136 "duplicate criterion id" errors, and i don't want to see that again

        loadBus.fire(new ForgeZRegister(this));
        loadBus.fire(new ForgeZRegister.Post());
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
