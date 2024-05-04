package org.violetmoon.zeta;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.bus.ZResult;
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

public class FabricZeta extends Zeta {
    public FabricZeta(String modid, Logger log) {
        super(modid, log, ZetaSide.fromClient(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT));
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public @Nullable String getModDisplayName(String modid) {
        // Cursed? Yes. Works? Yes.
        final String[] displayName = {"Unknown"};

        FabricLoader.getInstance()
                .getModContainer(modid)
                .ifPresent(mod -> displayName[0] = mod.getMetadata().getName());

        return displayName[0];
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
        return new FabricZetaRegistry(this);
    }

    @Override
    public CraftingExtensionsRegistry createCraftingExtensionsRegistry() {
        return new ForgeCraftingExtensionsRegistry();
    }

    @Override
    public BrewingRegistry createBrewingRegistry() {
        return new BrewingRegistry(this);
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
        return new FabricZetaNetworkHandler(this, protocolVersion);
    }

    @Override
    public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
    }

    @Override
    public <E, T extends E> T fireExternalEvent(T impl) {
        if(impl instanceof ZGatherAdvancementModifiers advancementModifiers)
            MinecraftForge.EVENT_BUS.post(new GatherAdvancementModifiersEvent(this, advancementModifiers));

        return impl;
    }

    @SuppressWarnings("duplicates")
    @Override
    public void start() {
        //TODO: sort these somehow lol

        //load
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(EventPriority.HIGHEST, this::registerHighest);
        modbus.addListener(this::commonSetup);
        modbus.addListener(this::loadComplete);
        modbus.addListener(this::entityAttributeCreation);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
        MinecraftForge.EVENT_BUS.addListener(this::tagsUpdated);

        // TODO FIX very ugly & bad
        modbus.addListener(EventPriority.LOWEST, CreativeTabManager::buildContents);
        modbus.addListener(ConfigEventDispatcher::configChanged);

        //play
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::rightClickBlockLow);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickItem);
        MinecraftForge.EVENT_BUS.addListener(this::livingDeath);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::livingDeathLowest);
        LivingEntityEvents.TICK.register(this::livingTick);
        MinecraftForge.EVENT_BUS.addListener(this::playNoteBlock);
        MinecraftForge.EVENT_BUS.addListener(this::lootTableLoad);
        MinecraftForge.EVENT_BUS.addListener(this::livingConversion);
        MinecraftForge.EVENT_BUS.addListener(this::livingConversionPre);
        MinecraftForge.EVENT_BUS.addListener(this::livingConversionPost);
        MinecraftForge.EVENT_BUS.addListener(this::anvilUpdate);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::anvilUpdateLowest);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::anvilUpdateHighest);
        MinecraftForge.EVENT_BUS.addListener(this::entityConstruct);
        UseEntityCallback.EVENT.register(this::entityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::entityMobGriefing);
        MinecraftForge.EVENT_BUS.addListener(this::livingDrops);
        MinecraftForge.EVENT_BUS.addListener(this::livingDropsLowest);
        MinecraftForge.EVENT_BUS.addListener(this::playerTickStart);
        MinecraftForge.EVENT_BUS.addListener(this::playerTickEnd);
        MinecraftForge.EVENT_BUS.addListener(this::babyEntitySpawn);
        MinecraftForge.EVENT_BUS.addListener(this::babyEntitySpawnLowest);
        MinecraftForge.EVENT_BUS.addListener(this::entityJoinLevel);

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::itemStackCaps);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::blockEntityCaps);
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, this::levelCaps);

        MinecraftForge.EVENT_BUS.addListener(this::serverTickStart);
        MinecraftForge.EVENT_BUS.addListener(this::serverTickEnd);
        MinecraftForge.EVENT_BUS.addListener(this::levelTickStart);
        MinecraftForge.EVENT_BUS.addListener(this::levelTickEnd);
        MinecraftForge.EVENT_BUS.addListener(this::playerInteract);
        MinecraftForge.EVENT_BUS.addListener(this::playerInteractEntityInteractSpecific);
        MinecraftForge.EVENT_BUS.addListener(this::playerInteractEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::playerInteractRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::playerInteractRightClickItem);
        MinecraftForge.EVENT_BUS.addListener(this::playerDestroyItem);
        MinecraftForge.EVENT_BUS.addListener(this::mobSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::mobSpawnFinalizeSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::mobSpawnFinalizeSpawnLowest);
        MinecraftForge.EVENT_BUS.addListener(this::livingChangeTarget);
        MinecraftForge.EVENT_BUS.addListener(this::sleepingLocationCheck);
        MinecraftForge.EVENT_BUS.addListener(this::villagerTrades);
        MinecraftForge.EVENT_BUS.addListener(this::anvilRepair);
        MinecraftForge.EVENT_BUS.addListener(this::player);
        MinecraftForge.EVENT_BUS.addListener(this::playerBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(this::playerClone);
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::entityItemPickup);
        MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
        MinecraftForge.EVENT_BUS.addListener(this::blockEntityPlace);
        MinecraftForge.EVENT_BUS.addListener(this::blockToolModification);
        MinecraftForge.EVENT_BUS.addListener(this::animalTame);
        MinecraftForge.EVENT_BUS.addListener(this::bonemeal);
        MinecraftForge.EVENT_BUS.addListener(this::entityTeleport);
        MinecraftForge.EVENT_BUS.addListener(this::livingFall);
        MinecraftForge.EVENT_BUS.addListener(this::wandererTrades);
        MinecraftForge.EVENT_BUS.addListener(this::furnaceFuelBurnTime);
        MinecraftForge.EVENT_BUS.addListener(this::itemTooltip);
    }

    private boolean registerDone = false;
    public void registerHighest(RegisterEvent e) {
        if(registerDone)
            return;

        registerDone = true; // do this *before* actually registering to prevent weird ??race conditions?? or something?
        //idk whats going on, all i know is i started the game, got a log with 136 "duplicate criterion id" errors, and i don't want to see that again

        loadBus.fire(new ZRegister(this));
        loadBus.fire(new ZRegister.Post());
    }

    public void commonSetup(FMLCommonSetupEvent e) {
        loadBus.fire(new FabricZCommonSetup(e), ZCommonSetup.class);
    }

    public void loadComplete(FMLLoadCompleteEvent e) {
        loadBus.fire(new FabricZLoadComplete(e), ZLoadComplete.class);
    }

    public void entityAttributeCreation(EntityAttributeCreationEvent e) {
        loadBus.fire(new FabricZEntityAttributeCreation(e), ZEntityAttributeCreation.class);
    }

    public void addReloadListener(AddReloadListenerEvent e) {
        loadBus.fire(new FabricZAddReloadListener(e), ZAddReloadListener.class);
    }

    public void tagsUpdated(TagsUpdatedEvent e) {
        loadBus.fire(new ZTagsUpdated());
    }

    public void rightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        playBus.fire(new FabricZRightClickBlock(e), ZRightClickBlock.class);
    }

    public void rightClickBlockLow(PlayerInteractEvent.RightClickBlock e) {
        playBus.fire(new FabricZRightClickBlock.Low(e), ZRightClickBlock.Low.class);
    }

    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        playBus.fire(new FabricZRightClickItem(e), ZRightClickItem.class);
    }

    public void livingDeath(LivingDeathEvent e) {
        playBus.fire(new FabricZLivingDeath(e), ZLivingDeath.class);
    }

    public void livingDeathLowest(LivingDeathEvent e) {
        playBus.fire(new FabricZLivingDeath.Lowest(e), ZLivingDeath.Lowest.class);
    }

    public void livingTick(LivingEntity e) {
        playBus.fire(new FabricZLivingTick(e), ZLivingTick.class);
    }

    public void playNoteBlock(NoteBlockEvent.Play e) {
        playBus.fire(new FabricZPlayNoteBlock(e), ZPlayNoteBlock.class);
    }

    public void lootTableLoad(LootTableLoadEvent e) {
        playBus.fire(new FabricZLootTableLoad(e), ZLootTableLoad.class);
    }

    public void livingConversion(LivingConversionEvent e) {
        playBus.fire(new FabricZLivingConversion(e), ZLivingConversion.class);
    }

    public void livingConversionPre(LivingConversionEvent.Pre e) {
        playBus.fire(new FabricZLivingConversion.Pre(e), ZLivingConversion.Pre.class);
    }

    public void livingConversionPost(LivingConversionEvent.Post e) {
        playBus.fire(new FabricZLivingConversion.Post(e), ZLivingConversion.Post.class);
    }

    public void anvilUpdate(AnvilUpdateEvent e) {
        playBus.fire(new FabricZAnvilUpdate(e), ZAnvilUpdate.class);
    }

    public void anvilUpdateLowest(AnvilUpdateEvent e) {
        playBus.fire(new FabricZAnvilUpdate.Lowest(e), ZAnvilUpdate.Lowest.class);
    }

    public void anvilUpdateHighest(AnvilUpdateEvent e) {
        playBus.fire(new FabricZAnvilUpdate.Highest(e), ZAnvilUpdate.Highest.class);
    }

    public void entityConstruct(EntityEvent.EntityConstructing e) {
        playBus.fire(new FabricZEntityConstruct(e), ZEntityConstruct.class);
    }

    public void entityInteract(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        playBus.fire(new FabricZEntityInteract(player, level, hand, entity), ZEntityInteract.class);
    }

    public void entityMobGriefing(EntityMobGriefingEvent e) {
        playBus.fire(new FabricZEntityMobGriefing(e), ZEntityMobGriefing.class);
    }

    public void livingDrops(LivingDropsEvent e) {
        playBus.fire(new FabricZLivingDrops(e), ZLivingDrops.class);
    }

    public void livingDropsLowest(LivingDropsEvent e) {
        playBus.fire(new FabricZLivingDrops.Lowest(e), ZLivingDrops.Lowest.class);
    }

    public void playerTickStart(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START)
            playBus.fire(new FabricZPlayerTick.Start(e), ZPlayerTick.Start.class);
    }

    public void playerTickEnd(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END)
            playBus.fire(new FabricZPlayerTick.End(e), ZPlayerTick.End.class);
    }

    public void babyEntitySpawn(BabyEntitySpawnEvent e) {
        playBus.fire(new FabricZBabyEntitySpawn(e), ZBabyEntitySpawn.class);
    }

    public void babyEntitySpawnLowest(BabyEntitySpawnEvent e) {
        playBus.fire(new FabricZBabyEntitySpawn.Lowest(e), ZBabyEntitySpawn.Lowest.class);
    }

    public void entityJoinLevel(EntityJoinLevelEvent e) {
        playBus.fire(new FabricZEntityJoinLevel(e), ZEntityJoinLevel.class);
    }

    public void itemStackCaps(AttachCapabilitiesEvent<ItemStack> e) {
        playBus.fire(new FabricZAttachCapabilities.ItemStackCaps(capabilityManager, e), ZAttachCapabilities.ItemStackCaps.class);
    }

    public void blockEntityCaps(AttachCapabilitiesEvent<BlockEntity> e) {
        playBus.fire(new FabricZAttachCapabilities.BlockEntityCaps(capabilityManager, e), ZAttachCapabilities.BlockEntityCaps.class);
    }

    public void levelCaps(AttachCapabilitiesEvent<Level> e) {
        playBus.fire(new FabricZAttachCapabilities.LevelCaps(capabilityManager, e), ZAttachCapabilities.LevelCaps.class);
    }

    public void serverTickStart(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.START)
            playBus.fire(new FabricZServerTick.Start(e), ZServerTick.Start.class);
    }

    public void serverTickEnd(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END)
            playBus.fire(new FabricZServerTick.End(e), ZServerTick.End.class);
    }

    public void levelTickStart(TickEvent.LevelTickEvent e) {
        if (e.phase == TickEvent.Phase.START)
            playBus.fire(new FabricZLevelTick.Start(e), ZLevelTick.Start.class);
    }

    public void levelTickEnd(TickEvent.LevelTickEvent e) {
        if (e.phase == TickEvent.Phase.END)
            playBus.fire(new FabricZLevelTick.End(e), ZLevelTick.End.class);
    }

    public void playerInteract(PlayerInteractEvent e) {
        playBus.fire(new FabricZPlayerInteract(e), ZPlayerInteract.class);
    }

    public void playerInteractEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
        playBus.fire(new FabricZPlayerInteract.EntityInteractSpecific(e), ZPlayerInteract.EntityInteractSpecific.class);
    }

    public void playerInteractEntityInteract(PlayerInteractEvent.EntityInteract e) {
        playBus.fire(new FabricZPlayerInteract.EntityInteract(e), ZPlayerInteract.EntityInteract.class);
    }

    public void playerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        playBus.fire(new FabricZPlayerInteract.RightClickBlock(e), ZPlayerInteract.RightClickBlock.class);
    }

    public void playerInteractRightClickItem(PlayerInteractEvent.RightClickItem e) {
        playBus.fire(new FabricZPlayerInteract.RightClickItem(e), ZPlayerInteract.RightClickItem.class);
    }

    public void playerDestroyItem(PlayerDestroyItemEvent e) {
        playBus.fire(new FabricZPlayerDestroyItem(e), ZPlayerDestroyItem.class);
    }

    public void mobSpawn(MobSpawnEvent e) {
        playBus.fire(new FabricZMobSpawnEvent(e), ZMobSpawnEvent.class);
    }

    public void mobSpawnFinalizeSpawn(MobSpawnEvent.FinalizeSpawn e) {
        playBus.fire(new FabricZMobSpawnEvent.FinalizeSpawn(e), ZMobSpawnEvent.CheckSpawn.class);
    }

    public void mobSpawnFinalizeSpawnLowest(MobSpawnEvent.FinalizeSpawn e) {
        playBus.fire(new FabricZMobSpawnEvent.FinalizeSpawn.Lowest(e), ZMobSpawnEvent.CheckSpawn.Lowest.class);
    }

    public void livingChangeTarget(LivingChangeTargetEvent e) {
        playBus.fire(new FabricZLivingChangeTarget(e), ZLivingChangeTarget.class);
    }

    public void sleepingLocationCheck(SleepingLocationCheckEvent e) {
        playBus.fire(new FabricZSleepingLocationCheck(e), ZSleepingLocationCheck.class);
    }

    public void entityItemPickup(EntityItemPickupEvent e) {
        playBus.fire(new FabricZEntityItemPickup(e), ZEntityItemPickup.class);
    }

    public void blockBreak(BlockEvent.BreakEvent e) {
        playBus.fire(new FabricZBlock.Break(e), ZBlock.Break.class);
    }

    public void blockEntityPlace(BlockEvent.EntityPlaceEvent e) {
        playBus.fire(new FabricZBlock.EntityPlace(e), ZBlock.EntityPlace.class);
    }

    public void blockToolModification(BlockEvent.BlockToolModificationEvent e) {
        playBus.fire(new FabricZBlock.BlockToolModification(e), ZBlock.BlockToolModification.class);
    }

    public void animalTame(AnimalTameEvent e) {
        playBus.fire(new FabricZAnimalTame(e), ZAnimalTame.class);
    }

    public void villagerTrades(VillagerTradesEvent e) {
        playBus.fire(new FabricZVillagerTrades(e), ZVillagerTrades.class);
    }

    public void anvilRepair(AnvilRepairEvent e) {
        playBus.fire(new FabricZAnvilRepair(e), ZAnvilRepair.class);
    }

    public void player(PlayerEvent e) {
        playBus.fire(new FabricZPlayer(e), ZPlayer.class);
    }

    public void playerBreakSpeed(PlayerEvent.BreakSpeed e) {
        playBus.fire(new FabricZPlayer.BreakSpeed(e), ZPlayer.BreakSpeed.class);
    }

    public void playerClone(PlayerEvent.Clone e) {
        playBus.fire(new FabricZPlayer.Clone(e), ZPlayer.Clone.class);
    }

    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        playBus.fire(new FabricZPlayer.LoggedIn(e), ZPlayer.LoggedIn.class);
    }

    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
        playBus.fire(new FabricZPlayer.LoggedOut(e), ZPlayer.LoggedOut.class);
    }

    public void bonemeal(BonemealEvent e) {
        playBus.fire(new FabricZBonemeal(e), ZBonemeal.class);
    }

    public void entityTeleport(EntityTeleportEvent e) {
        playBus.fire(new FabricZEntityTeleport(e), ZEntityTeleport.class);
    }

    public void livingFall(LivingFallEvent e) {
        playBus.fire(new FabricZLivingFall(e), ZLivingFall.class);
    }

    public void wandererTrades(WandererTradesEvent e) {
        playBus.fire(new FabricZWandererTrades(e), ZWandererTrades.class);
    }

    public void furnaceFuelBurnTime(FurnaceFuelBurnTimeEvent e) {
        playBus.fire(new FabricZFurnaceFuelBurnTime(e), ZFurnaceFuelBurnTime.class);
    }

    public void itemTooltip(ItemTooltipEvent e) {
        playBus.fire(new FabricZItemTooltip(e), ZItemTooltip.class);
    }

    public static ZResult from(BaseEvent.Result r) {
        return switch(r) {
            case DENY -> ZResult.DENY;
            case DEFAULT -> ZResult.DEFAULT;
            case ALLOW -> ZResult.ALLOW;
        };
    }

    public static BaseEvent.Result to(ZResult r) {
        return switch(r) {
            case DENY -> BaseEvent.Result.DENY;
            case DEFAULT -> BaseEvent.Result.DEFAULT;
            case ALLOW -> BaseEvent.Result.ALLOW;
        };
    }
}