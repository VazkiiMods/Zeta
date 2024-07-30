package org.violetmoon.zetaimplforge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
import org.violetmoon.zeta.event.bus.*;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.ZAttachCapabilities;
import org.violetmoon.zeta.event.play.loading.ZLootTableLoad;
import org.violetmoon.zeta.event.play.loading.ZVillagerTrades;
import org.violetmoon.zeta.event.play.loading.ZWandererTrades;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.api.GatherAdvancementModifiersEvent;
import org.violetmoon.zetaimplforge.block.IForgeBlockBlockExtensions;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.config.ForgeBackedConfig;
import org.violetmoon.zetaimplforge.config.TerribleForgeConfigHackery;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZAttachCapabilities;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZLootTableLoad;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZVillagerTrades;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZWandererTrades;
import org.violetmoon.zetaimplforge.item.IForgeItemItemExtensions;
import org.violetmoon.zetaimplforge.network.ForgeZetaNetworkHandler;
import org.violetmoon.zetaimplforge.registry.ForgeBrewingRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeZetaRegistry;
import org.violetmoon.zetaimplforge.util.ForgeRaytracingUtil;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
    public ForgeZeta(String modid, Logger log) {
        super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()),
                new ForgeZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log,
                        FMLJavaModLoadingContext.get().getModEventBus()),
                new ForgeZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class, null,
                        MinecraftForge.EVENT_BUS)
        );

        uglyPopulateForgePlayBus();
        uglyPopulateForgeLoadBus();
    }

    // adds known events sub classes to the bus
    private void uglyPopulateForgeLoadBus(){
        var bus = (ForgeZetaEventBus<IZetaLoadEvent>) loadBus;
        bus.registerSubClass(ZCommonSetup.class, ForgeZCommonSetup.class);
        bus.registerSubClass(ZAddReloadListener.class, ForgeZAddReloadListener.class);
        bus.registerSubClass(ZEntityAttributeCreation.class, ForgeZEntityAttributeCreation.class);
        bus.registerSubClass(ZModulesReady.class, ForgeZModulesReady.class);
        bus.registerSubClass(ZRegister.class, ForgeZRegister.class);
        bus.registerSubClass(ZTagsUpdated.class, ForgeZTagsUpdated.class);
        bus.registerSubClass(ZConfigChanged.class, ForgeZConfigChange.class);
    }

    private void uglyPopulateForgePlayBus() {
        var bus = (ForgeZetaEventBus<IZetaPlayEvent>) playBus;
        bus.registerSubClass(ZAnvilRepair.class, ForgeZAnvilRepair.class);
        bus.registerSubClass(ZBabyEntitySpawn.class, ForgeZBabyEntitySpawn.class);
        bus.registerSubClass(ZBlock.Break.class, ForgeZBlock.Break.class);
        bus.registerSubClass(ZBlock.EntityPlace.class, ForgeZBlock.EntityPlace.class);
        bus.registerSubClass(ZBlock.BlockToolModification.class, ForgeZBlock.BlockToolModification.class);
        bus.registerSubClass(ZBonemeal.class, ForgeZBonemeal.class);
        bus.registerSubClass(ZEntityConstruct.class, ForgeZEntityConstruct.class);
        bus.registerSubClass(ZEntityInteract.class, ForgeZEntityInteract.class);
        bus.registerSubClass(ZEntityItemPickup.class, ForgeZEntityItemPickup.class);
        bus.registerSubClass(ZEntityJoinLevel.class, ForgeZEntityJoinLevel.class);
        bus.registerSubClass(ZEntityMobGriefing.class, ForgeZEntityMobGriefing.class);
        bus.registerSubClass(ZEntityTeleport.class, ForgeZEntityTeleport.class);
        bus.registerSubClass(ZItemTooltip.class, ForgeZItemTooltip.class);
        bus.registerSubClass(ZLivingChangeTarget.class, ForgeZLivingChangeTarget.class);
        bus.registerSubClass(ZLivingConversion.class, ForgeZLivingConversion.class);
        bus.registerSubClass(ZLivingConversion.Pre.class, ForgeZLivingConversion.Pre.class);
        bus.registerSubClass(ZLivingConversion.Post.class, ForgeZLivingConversion.Post.class);
        bus.registerSubClass(ZLivingDeath.class, ForgeZLivingDeath.class);
        bus.registerSubClass(ZLivingDeath.Lowest.class, ForgeZLivingDeath.Lowest.class);
        bus.registerSubClass(ZLivingDrops.class, ForgeZLivingDrops.class);
        bus.registerSubClass(ZLivingDrops.Lowest.class, ForgeZLivingDrops.Lowest.class);
        bus.registerSubClass(ZLivingFall.class, ForgeZLivingFall.class);
        bus.registerSubClass(ZLivingTick.class, ForgeZLivingTick.class);
        bus.registerSubClass(ZMobSpawnEvent.class, ForgeZMobSpawnEvent.class);
        bus.registerSubClass(ZMobSpawnEvent.CheckSpawn.class, ForgeZMobSpawnEvent.FinalizeSpawn.class);
        bus.registerSubClass(ZMobSpawnEvent.CheckSpawn.Lowest.class, ForgeZMobSpawnEvent.FinalizeSpawn.Lowest.class);
        bus.registerSubClass(ZPlayNoteBlock.class, ForgeZPlayNoteBlock.class);
        bus.registerSubClass(ZPlayer.class, ForgeZPlayer.class);
        bus.registerSubClass(ZPlayer.BreakSpeed.class, ForgeZPlayer.BreakSpeed.class);
        bus.registerSubClass(ZPlayer.Clone.class, ForgeZPlayer.Clone.class);
        bus.registerSubClass(ZPlayerDestroyItem.class, ForgeZPlayerDestroyItem.class);
        bus.registerSubClass(ZPlayer.LoggedIn.class, ForgeZPlayer.LoggedIn.class);
        bus.registerSubClass(ZPlayer.LoggedOut.class, ForgeZPlayer.LoggedOut.class);
        bus.registerSubClass(ZPlayerTick.Start.class, ForgeZPlayerTick.Start.class);
        bus.registerSubClass(ZPlayerTick.End.class, ForgeZPlayerTick.End.class);
        bus.registerSubClass(ZPlayerInteract.class, ForgeZPlayerInteract.class);
        bus.registerSubClass(ZPlayerInteract.EntityInteractSpecific.class, ForgeZPlayerInteract.EntityInteractSpecific.class);
        bus.registerSubClass(ZPlayerInteract.EntityInteract.class, ForgeZPlayerInteract.EntityInteract.class);
        bus.registerSubClass(ZPlayerInteract.RightClickBlock.class, ForgeZPlayerInteract.RightClickBlock.class);
        bus.registerSubClass(ZPlayerInteract.RightClickItem.class, ForgeZPlayerInteract.RightClickItem.class);
        bus.registerSubClass(ZRightClickBlock.class, ForgeZRightClickBlock.class);
        bus.registerSubClass(ZRightClickBlock.Low.class, ForgeZRightClickBlock.Low.class);
        bus.registerSubClass(ZRightClickItem.class, ForgeZRightClickItem.class);
        bus.registerSubClass(ZLootTableLoad.class, ForgeZLootTableLoad.class);
        bus.registerSubClass(ZVillagerTrades.class, ForgeZVillagerTrades.class);
        bus.registerSubClass(ZWandererTrades.class, ForgeZWandererTrades.class);

        bus.registerSubClass(ZAttachCapabilities.BlockEntityCaps.class, ForgeZAttachCapabilities.BlockEntityCaps.class);
        bus.registerSubClass(ZAttachCapabilities.ItemStackCaps.class, ForgeZAttachCapabilities.ItemStackCaps.class);
        bus.registerSubClass(ZAttachCapabilities.LevelCaps.class, ForgeZAttachCapabilities.LevelCaps.class);

        // zeta specific ones

        bus.registerSubClass(ZRecipeCrawl.Digest.class, ForgeZRecipeCrawl.Digest.class);
        bus.registerSubClass(ZRecipeCrawl.Reset.class, ForgeZRecipeCrawl.Reset.class);
        bus.registerSubClass(ZRecipeCrawl.Starting.class, ForgeZRecipeCrawl.Starting.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Cooking.class, ForgeZRecipeCrawl.Visit.Cooking.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Custom.class, ForgeZRecipeCrawl.Visit.Custom.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Misc.class, ForgeZRecipeCrawl.Visit.Misc.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Shaped.class, ForgeZRecipeCrawl.Visit.Shaped.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Shapeless.class, ForgeZRecipeCrawl.Visit.Shapeless.class);

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
        ForgeConfigSpec.Builder bob = new ForgeConfigSpec.Builder();
        ForgeBackedConfig forge = new ForgeBackedConfig(rootSection, bob);
        ForgeConfigSpec spec = bob.build();

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

    @Override
    public ZetaCapabilityManager createCapabilityManager() {
        return new ForgeCapabilityManager();
    }

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
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
    }

    //TODO:
    @Override
    public <E, T extends E> T fireExternalEvent(T impl) {
        if (impl instanceof ZGatherAdvancementModifiers advancementModifiers)
            MinecraftForge.EVENT_BUS.post(new GatherAdvancementModifiersEvent(this, advancementModifiers));

        return impl;
    }

    @SuppressWarnings("duplicates")
    @Override
    public void start() {
        //load
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(EventPriority.LOWEST, CreativeTabManager::buildContents);
        modbus.addListener(ConfigEventDispatcher::configChanged);
    }

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
