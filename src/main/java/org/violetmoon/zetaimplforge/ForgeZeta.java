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
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.client.event.load.*;
import org.violetmoon.zeta.client.event.play.*;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.bus.*;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.*;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.api.ForgeZGatherAdvancementModifiers;
import org.violetmoon.zetaimplforge.block.IForgeBlockBlockExtensions;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.client.event.load.*;
import org.violetmoon.zetaimplforge.client.event.play.*;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.config.ForgeBackedConfig;
import org.violetmoon.zetaimplforge.config.TerribleForgeConfigHackery;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.*;
import org.violetmoon.zetaimplforge.item.IForgeItemItemExtensions;
import org.violetmoon.zetaimplforge.network.ForgeZetaNetworkHandler;
import org.violetmoon.zetaimplforge.registry.ForgeBrewingRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeZetaRegistry;
import org.violetmoon.zetaimplforge.util.ForgeRaytracingUtil;

import java.util.function.Function;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
    public ForgeZeta(String modid, Logger log) {
        super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()));
    }

    @Override
    protected ZetaEventBus<IZetaLoadEvent> createLoadBus() {
        //return new StandaloneZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log);

        var bus = new ForgeZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log,
                FMLJavaModLoadingContext.get().getModEventBus(), Event.class);

        // adds known events subclasses to the bus
        bus.registerSubClass(ZCommonSetup.class, ForgeZCommonSetup.class);
        bus.registerSubClass(ZEntityAttributeCreation.class, ForgeZEntityAttributeCreation.class);
        bus.registerSubClass(ZModulesReady.class, ForgeZModulesReady.class);
        bus.registerSubClass(ZRegister.class, ForgeZRegister.class);
        bus.registerSubClass(ZRegister.Post.class, ForgeZRegister.Post.class);
        bus.registerSubClass(ZConfigChanged.class, ForgeZConfigChange.class);
        bus.registerSubClass(ZLoadComplete.class, ForgeZLoadComplete.class);

        // client ones again?
        bus.registerSubClass(ZAddModels.class, ForgeZAddModels.class);
        bus.registerSubClass(ZAddModelLayers.class, ForgeZAddModelLayers.class);
        bus.registerSubClass(ZClientSetup.class, ForgeZClientSetup.class);
        bus.registerSubClass(ZKeyMapping.class, ForgeZKeyMapping.class);
        bus.registerSubClass(ZModel.RegisterGeometryLoaders.class, ForgeZModel.RegisterGeometryLoaders.class);
        bus.registerSubClass(ZModel.RegisterAdditional.class, ForgeZModel.RegisterAdditional.class);
        bus.registerSubClass(ZModel.BakingCompleted.class, ForgeZModel.BakingCompleted.class);
        bus.registerSubClass(ZModel.ModifyBakingResult.class, ForgeZModel.ModifyBakingResult.class);
        bus.registerSubClass(ZRegisterLayerDefinitions.class, ForgeZRegisterLayerDefinitions.class);
        bus.registerSubClass(ZTooltipComponents.class, ForgeZTooltipComponents.class);
        bus.registerSubClass(ZFirstClientTick.class, ForgeZFirstClientTick.class);

        bus.registerSubClass(ZAddBlockColorHandlers.class, ForgeZAddBlockColorHandlers.class,
                (Function<RegisterColorHandlersEvent.Block, ForgeZAddBlockColorHandlers>) inner ->
                        new ForgeZAddBlockColorHandlers(inner, this.registry));
        bus.registerSubClass(ZAddItemColorHandlers.class, ForgeZAddItemColorHandlers.class,
                (Function<RegisterColorHandlersEvent.Item, ForgeZAddItemColorHandlers>) inner ->
                        new ForgeZAddItemColorHandlers(inner, this.registry));
        return bus;
    }

    @Override
    protected ForgeZetaEventBus<IZetaPlayEvent, ?> createPlayBus() {
        var bus = new ForgeZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class, log, MinecraftForge.EVENT_BUS, Event.class);
        bus.registerSubClass(ZAnvilRepair.class, ForgeZAnvilRepair.class);
        bus.registerSubClass(ZTagsUpdated.class, ForgeZTagsUpdated.class);
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
        bus.registerSubClass(ZFurnaceFuelBurnTime.class, ForgeZFurnaceFuelBurnTime.class);
        bus.registerSubClass(ZGatherAdditionalFlags.class, ForgeZGatherAdditionalFlags.class);
        bus.registerSubClass(ZServerTick.Start.class, ForgeZServerTick.Start.class);
        bus.registerSubClass(ZServerTick.End.class, ForgeZServerTick.End.class);
        bus.registerSubClass(ZAddReloadListener.class, ForgeZAddReloadListener.class);
        bus.registerSubClass(ZGatherAdvancementModifiers.class, ForgeZGatherAdvancementModifiers.class);

        //Hmm client events here? maybe i should move them

        bus.registerSubClass(ZClientTick.class, ForgeZClientTick.class);
        bus.registerSubClass(ZEarlyRender.class, ForgeZEarlyRender.class);
        bus.registerSubClass(ZGatherTooltipComponents.class, ForgeZGatherTooltipComponents.class);
        bus.registerSubClass(ZHighlightBlock.class, ForgeZHighlightBlock.class);
        bus.registerSubClass(ZInput.MouseButton.class, ForgeZInput.MouseButton.class);
        bus.registerSubClass(ZInput.Key.class, ForgeZInput.Key.class);
        bus.registerSubClass(ZInputUpdate.class, ForgeZInputUpdate.class);
        bus.registerSubClass(ZRenderContainerScreen.class, ForgeZRenderContainerScreen.class);
        bus.registerSubClass(ZRenderLiving.class, ForgeZRenderLiving.class);
        bus.registerSubClass(ZRenderPlayer.class, ForgeZRenderPlayer.class);
        bus.registerSubClass(ZRenderTick.class, ForgeZRenderTick.class);
        bus.registerSubClass(ZRenderTooltip.GatherComponents.class, ForgeZRenderTooltip.GatherComponents.class);
        bus.registerSubClass(ZScreen.Opening.class, ForgeZScreen.Opening.class);
        bus.registerSubClass(ZScreen.CharacterTyped.class, ForgeZScreen.CharacterTyped.class);
        bus.registerSubClass(ZScreen.Init.class, ForgeZScreen.Init.class);
        bus.registerSubClass(ZScreen.KeyPressed.class, ForgeZScreen.KeyPressed.class);
        bus.registerSubClass(ZScreen.MouseScrolled.class, ForgeZScreen.MouseScrolled.class);
        bus.registerSubClass(ZScreen.MouseButtonPressed.class, ForgeZScreen.MouseButtonPressed.class);
        bus.registerSubClass(ZScreen.Render.class, ForgeZScreen.Render.class);
        bus.registerSubClass(ZRenderGuiOverlay.ArmorLevel.Pre.class, ForgeZRenderGuiOverlay.ArmorLevel.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.ArmorLevel.Post.class, ForgeZRenderGuiOverlay.ArmorLevel.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.Crosshair.Pre.class, ForgeZRenderGuiOverlay.Crosshair.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.Crosshair.Post.class, ForgeZRenderGuiOverlay.Crosshair.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.DebugText.Pre.class, ForgeZRenderGuiOverlay.DebugText.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.DebugText.Post.class, ForgeZRenderGuiOverlay.DebugText.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.Hotbar.Pre.class, ForgeZRenderGuiOverlay.Hotbar.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.Hotbar.Post.class, ForgeZRenderGuiOverlay.Hotbar.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.PlayerHealth.Pre.class, ForgeZRenderGuiOverlay.PlayerHealth.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.PlayerHealth.Post.class, ForgeZRenderGuiOverlay.PlayerHealth.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.PotionIcons.Pre.class, ForgeZRenderGuiOverlay.PotionIcons.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.PotionIcons.Post.class, ForgeZRenderGuiOverlay.PotionIcons.Post.class);
        bus.registerSubClass(ZRenderGuiOverlay.ChatPanel.Pre.class, ForgeZRenderGuiOverlay.ChatPanel.Pre.class);
        bus.registerSubClass(ZRenderGuiOverlay.ChatPanel.Post.class, ForgeZRenderGuiOverlay.ChatPanel.Post.class);
        bus.registerSubClass(ZScreenshot.class, ForgeZScreenshot.class);


        //this is ugly. generic events here
        Zeta zeta = this;
        bus.registerSubClassWithGeneric(ZAttachCapabilities.BlockEntityCaps.class,
                ForgeZAttachCapabilities.BlockEntityCaps.class,
                (Function<AttachCapabilitiesEvent<BlockEntity>, ForgeZAttachCapabilities.BlockEntityCaps>) inner ->
                        new ForgeZAttachCapabilities.BlockEntityCaps(zeta.capabilityManager, inner),
                BlockEntity.class);
        bus.registerSubClassWithGeneric(ZAttachCapabilities.ItemStackCaps.class,
                ForgeZAttachCapabilities.ItemStackCaps.class,
                (Function<AttachCapabilitiesEvent<ItemStack>, ForgeZAttachCapabilities.ItemStackCaps>) inner ->
                        new ForgeZAttachCapabilities.ItemStackCaps(zeta.capabilityManager, inner),
                ItemStack.class);
        bus.registerSubClassWithGeneric(ZAttachCapabilities.LevelCaps.class,
                ForgeZAttachCapabilities.LevelCaps.class,
                (Function<AttachCapabilitiesEvent<Level>, ForgeZAttachCapabilities.LevelCaps>) inner ->
                        new ForgeZAttachCapabilities.LevelCaps(zeta.capabilityManager, inner),
                Level.class);

        // zeta specific ones

        bus.registerSubClass(ZRecipeCrawl.Digest.class, ForgeZRecipeCrawl.Digest.class);
        bus.registerSubClass(ZRecipeCrawl.Reset.class, ForgeZRecipeCrawl.Reset.class);
        bus.registerSubClass(ZRecipeCrawl.Starting.class, ForgeZRecipeCrawl.Starting.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Cooking.class, ForgeZRecipeCrawl.Visit.Cooking.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Custom.class, ForgeZRecipeCrawl.Visit.Custom.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Misc.class, ForgeZRecipeCrawl.Visit.Misc.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Shaped.class, ForgeZRecipeCrawl.Visit.Shaped.class);
        bus.registerSubClass(ZRecipeCrawl.Visit.Shapeless.class, ForgeZRecipeCrawl.Visit.Shapeless.class);

        return bus;
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

    @SuppressWarnings("duplicates")
    @Override
    public void start() {
        //load
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(EventPriority.LOWEST, CreativeTabManager::buildContents);
        modbus.addListener(ConfigEventDispatcher::configChanged);

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
