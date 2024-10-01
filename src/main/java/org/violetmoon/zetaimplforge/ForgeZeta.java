package org.violetmoon.zetaimplforge;

import net.minecraft.core.BlockPos;
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
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.api.GatherAdvancementModifiersEvent;
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
import org.violetmoon.zetaimplforge.item.IForgeItemItemExtensions;
import org.violetmoon.zetaimplforge.registry.ForgeBrewingRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;
import org.violetmoon.zetaimplforge.registry.ForgeZetaRegistry;
import org.violetmoon.zetaimplforge.util.ForgeRaytracingUtil;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
	public ForgeZeta(String modid, Logger log) {
		super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()));
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
		return (resloc, potted) -> ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(resloc, () -> potted);
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
	public PlayerInteractEvent.RightClickBlock fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
		return NeoForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
	}

	@Override
	public <E, T extends E> T fireExternalEvent(T impl) {
		if(impl instanceof ZGatherAdvancementModifiers advancementModifiers)
			NeoForge.EVENT_BUS.post(new GatherAdvancementModifiersEvent(this, advancementModifiers));

		return impl;
	}

	@SuppressWarnings("duplicates")
	@Override
	public void start(IEventBus modbus) {
		//TODO: sort these somehow lol

		//load
		modbus.addListener(EventPriority.HIGHEST, this::registerHighest);
		modbus.addListener(this::commonSetup);
		modbus.addListener(this::loadComplete);
		modbus.addListener(this::entityAttributeCreation);
		NeoForge.EVENT_BUS.addListener(this::addReloadListener);
		NeoForge.EVENT_BUS.addListener(this::tagsUpdated);

		// TODO FIX very ugly & bad
		modbus.addListener(EventPriority.LOWEST, CreativeTabManager::buildContents);
		modbus.addListener(ConfigEventDispatcher::configChanged);

		//play
		NeoForge.EVENT_BUS.addListener(this::rightClickBlock);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, this::rightClickBlockLow);
		NeoForge.EVENT_BUS.addListener(this::rightClickItem);
		NeoForge.EVENT_BUS.addListener(this::livingDeath);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::livingDeathLowest);
		//NeoForge.EVENT_BUS.addListener(this::livingTick);
		NeoForge.EVENT_BUS.addListener(this::playNoteBlock);
		NeoForge.EVENT_BUS.addListener(this::lootTableLoad);
		NeoForge.EVENT_BUS.addListener(this::livingConversion);
		NeoForge.EVENT_BUS.addListener(this::livingConversionPre);
		NeoForge.EVENT_BUS.addListener(this::livingConversionPost);
		NeoForge.EVENT_BUS.addListener(this::anvilUpdate);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::anvilUpdateLowest);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::anvilUpdateHighest);
		NeoForge.EVENT_BUS.addListener(this::entityConstruct);
		NeoForge.EVENT_BUS.addListener(this::entityInteract);
		NeoForge.EVENT_BUS.addListener(this::entityMobGriefing);
		NeoForge.EVENT_BUS.addListener(this::livingDrops);
		NeoForge.EVENT_BUS.addListener(this::livingDropsLowest);
		NeoForge.EVENT_BUS.addListener(this::playerTickStart);
		NeoForge.EVENT_BUS.addListener(this::playerTickEnd);
		NeoForge.EVENT_BUS.addListener(this::babyEntitySpawn);
		NeoForge.EVENT_BUS.addListener(this::babyEntitySpawnLowest);
		NeoForge.EVENT_BUS.addListener(this::entityJoinLevel);

		/*NeoForge.EVENT_BUS.addGenericListener(ItemStack.class, this::itemStackCaps);
		NeoForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::blockEntityCaps);
		NeoForge.EVENT_BUS.addGenericListener(Level.class, this::levelCaps);*/

		NeoForge.EVENT_BUS.addListener(this::serverTickStart);
		NeoForge.EVENT_BUS.addListener(this::serverTickEnd);
		NeoForge.EVENT_BUS.addListener(this::levelTickStart);
		NeoForge.EVENT_BUS.addListener(this::levelTickEnd);
		NeoForge.EVENT_BUS.addListener(this::playerInteract);
		NeoForge.EVENT_BUS.addListener(this::playerInteractEntityInteractSpecific);
		NeoForge.EVENT_BUS.addListener(this::playerInteractEntityInteract);
		NeoForge.EVENT_BUS.addListener(this::playerInteractRightClickBlock);
		NeoForge.EVENT_BUS.addListener(this::playerInteractRightClickItem);
		NeoForge.EVENT_BUS.addListener(this::playerDestroyItem);
		NeoForge.EVENT_BUS.addListener(this::mobSpawn);
		//NeoForge.EVENT_BUS.addListener(this::mobSpawnFinalizeSpawn);
		//NeoForge.EVENT_BUS.addListener(this::mobSpawnFinalizeSpawnLowest);
		NeoForge.EVENT_BUS.addListener(this::livingChangeTarget);
		//NeoForge.EVENT_BUS.addListener(this::sleepingLocationCheck);
		NeoForge.EVENT_BUS.addListener(this::villagerTrades);
		NeoForge.EVENT_BUS.addListener(this::anvilRepair);
		NeoForge.EVENT_BUS.addListener(this::player);
		NeoForge.EVENT_BUS.addListener(this::playerBreakSpeed);
		NeoForge.EVENT_BUS.addListener(this::playerClone);
		NeoForge.EVENT_BUS.addListener(this::playerLoggedIn);
		NeoForge.EVENT_BUS.addListener(this::playerLoggedOut);
		NeoForge.EVENT_BUS.addListener(this::entityItemPickup);
		NeoForge.EVENT_BUS.addListener(this::blockBreak);
		NeoForge.EVENT_BUS.addListener(this::blockEntityPlace);
		//NeoForge.EVENT_BUS.addListener(this::blockToolModification);
		NeoForge.EVENT_BUS.addListener(this::animalTame);
		NeoForge.EVENT_BUS.addListener(this::bonemeal);
		NeoForge.EVENT_BUS.addListener(this::entityTeleport);
		NeoForge.EVENT_BUS.addListener(this::livingFall);
		NeoForge.EVENT_BUS.addListener(this::wandererTrades);
		NeoForge.EVENT_BUS.addListener(this::furnaceFuelBurnTime);
		NeoForge.EVENT_BUS.addListener(this::itemTooltip);
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
		loadBus.fire(new ForgeZCommonSetup(e), ZCommonSetup.class);
	}

	public void loadComplete(FMLLoadCompleteEvent e) {
		loadBus.fire(new ForgeZLoadComplete(e), ZLoadComplete.class);
	}

	public void entityAttributeCreation(EntityAttributeCreationEvent e) {
		loadBus.fire(new ForgeZEntityAttributeCreation(e), ZEntityAttributeCreation.class);
	}

	public void addReloadListener(AddReloadListenerEvent e) {
		loadBus.fire(new ForgeZAddReloadListener(e), ZAddReloadListener.class);
	}

	public void tagsUpdated(TagsUpdatedEvent e) {
		loadBus.fire(new ZTagsUpdated());
	}

	public void rightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZRightClickBlock(e), ZRightClickBlock.class);
	}

	public void rightClickBlockLow(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZRightClickBlock.Low(e), ZRightClickBlock.Low.class);
	}

	public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
		playBus.fire(new ForgeZRightClickItem(e), ZRightClickItem.class);
	}

	public void livingDeath(LivingDeathEvent e) {
		playBus.fire(new ForgeZLivingDeath(e), ZLivingDeath.class);
	}

	public void livingDeathLowest(LivingDeathEvent e) {
		playBus.fire(new ForgeZLivingDeath.Lowest(e), ZLivingDeath.Lowest.class);
	}

	/*public void livingTick(LivingEvent.LivingTickEvent e) {
		playBus.fire(new ForgeZLivingTick(e), ZLivingTick.class);
	}*/

	public void playNoteBlock(NoteBlockEvent.Play e) {
		playBus.fire(new ForgeZPlayNoteBlock(e), ZPlayNoteBlock.class);
	}

	public void lootTableLoad(LootTableLoadEvent e) {
		playBus.fire(new ForgeZLootTableLoad(e), ZLootTableLoad.class);
	}

	public void livingConversion(LivingConversionEvent e) {
		playBus.fire(new ForgeZLivingConversion(e), ZLivingConversion.class);
	}

	public void livingConversionPre(LivingConversionEvent.Pre e) {
		playBus.fire(new ForgeZLivingConversion.Pre(e), ZLivingConversion.Pre.class);
	}

	public void livingConversionPost(LivingConversionEvent.Post e) {
		playBus.fire(new ForgeZLivingConversion.Post(e), ZLivingConversion.Post.class);
	}

	public void anvilUpdate(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate(e), ZAnvilUpdate.class);
	}

	public void anvilUpdateLowest(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate.Lowest(e), ZAnvilUpdate.Lowest.class);
	}

	public void anvilUpdateHighest(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate.Highest(e), ZAnvilUpdate.Highest.class);
	}

	public void entityConstruct(EntityEvent.EntityConstructing e) {
		playBus.fire(new ForgeZEntityConstruct(e), ZEntityConstruct.class);
	}

	public void entityInteract(PlayerInteractEvent.EntityInteract e) {
		playBus.fire(new ForgeZEntityInteract(e), ZEntityInteract.class);
	}

	public void entityMobGriefing(EntityMobGriefingEvent e) {
		playBus.fire(new ForgeZEntityMobGriefing(e), ZEntityMobGriefing.class);
	}

	public void livingDrops(LivingDropsEvent e) {
		playBus.fire(new ForgeZLivingDrops(e), ZLivingDrops.class);
	}

	public void livingDropsLowest(LivingDropsEvent e) {
		playBus.fire(new ForgeZLivingDrops.Lowest(e), ZLivingDrops.Lowest.class);
	}

	public void playerTickStart(PlayerTickEvent e) {
		if (e instanceof PlayerTickEvent.Pre)
			playBus.fire(new ForgeZPlayerTick.Pre(e), ZPlayerTick.Start.class);
	}

	public void playerTickEnd(PlayerTickEvent e) {
		if (e instanceof PlayerTickEvent.Post)
			playBus.fire(new ForgeZPlayerTick.Post(e), ZPlayerTick.End.class);
	}

	public void babyEntitySpawn(BabyEntitySpawnEvent e) {
		playBus.fire(new ForgeZBabyEntitySpawn(e), ZBabyEntitySpawn.class);
	}

	public void babyEntitySpawnLowest(BabyEntitySpawnEvent e) {
		playBus.fire(new ForgeZBabyEntitySpawn.Lowest(e), ZBabyEntitySpawn.Lowest.class);
	}

	public void entityJoinLevel(EntityJoinLevelEvent e) {
		playBus.fire(new ForgeZEntityJoinLevel(e), ZEntityJoinLevel.class);
	}

	/*public void itemStackCaps(AttachCapabilitiesEvent<ItemStack> e) {
		playBus.fire(new ForgeZAttachCapabilities.ItemStackCaps(capabilityManager, e), ZAttachCapabilities.ItemStackCaps.class);
	}

	public void blockEntityCaps(AttachCapabilitiesEvent<BlockEntity> e) {
		playBus.fire(new ForgeZAttachCapabilities.BlockEntityCaps(capabilityManager, e), ZAttachCapabilities.BlockEntityCaps.class);
	}

	public void levelCaps(AttachCapabilitiesEvent<Level> e) {
		playBus.fire(new ForgeZAttachCapabilities.LevelCaps(capabilityManager, e), ZAttachCapabilities.LevelCaps.class);
	}*/

	public void serverTickStart(ServerTickEvent e) {
		if (e instanceof ServerTickEvent.Pre)
			playBus.fire(new ForgeZServerTick.Pre(e), ZServerTick.Start.class);
	}

	public void serverTickEnd(ServerTickEvent e) {
		if (e instanceof ServerTickEvent.Post)
			playBus.fire(new ForgeZServerTick.Post(e), ZServerTick.End.class);
	}

	public void levelTickStart(LevelTickEvent e) {
		if (e instanceof LevelTickEvent.Pre)
			playBus.fire(new ForgeZLevelTick.Start(e), ZLevelTick.Start.class);
	}

	public void levelTickEnd(LevelTickEvent e) {
		if (e instanceof LevelTickEvent.Post)
			playBus.fire(new ForgeZLevelTick.End(e), ZLevelTick.End.class);
	}

	public void playerInteract(PlayerInteractEvent e) {
		playBus.fire(new ForgeZPlayerInteract(e), ZPlayerInteract.class);
	}

	public void playerInteractEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
		playBus.fire(new ForgeZPlayerInteract.EntityInteractSpecific(e), ZPlayerInteract.EntityInteractSpecific.class);
	}

	public void playerInteractEntityInteract(PlayerInteractEvent.EntityInteract e) {
		playBus.fire(new ForgeZPlayerInteract.EntityInteract(e), ZPlayerInteract.EntityInteract.class);
	}

	public void playerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZPlayerInteract.RightClickBlock(e), ZPlayerInteract.RightClickBlock.class);
	}

	public void playerInteractRightClickItem(PlayerInteractEvent.RightClickItem e) {
		playBus.fire(new ForgeZPlayerInteract.RightClickItem(e), ZPlayerInteract.RightClickItem.class);
	}

	public void playerDestroyItem(PlayerDestroyItemEvent e) {
		playBus.fire(new ForgeZPlayerDestroyItem(e), ZPlayerDestroyItem.class);
	}

	public void mobSpawn(MobSpawnEvent e) {
		playBus.fire(new ForgeZMobSpawnEvent(e), ZMobSpawnEvent.class);
	}

	/*public void mobSpawnFinalizeSpawn(MobSpawnEvent.FinalizeSpawn e) {
		playBus.fire(new ForgeZMobSpawnEvent.FinalizeSpawn(e), ZMobSpawnEvent.CheckSpawn.class);
	}

	public void mobSpawnFinalizeSpawnLowest(MobSpawnEvent.FinalizeSpawn e) {
		playBus.fire(new ForgeZMobSpawnEvent.FinalizeSpawn.Lowest(e), ZMobSpawnEvent.CheckSpawn.Lowest.class);
	}*/

	public void livingChangeTarget(LivingChangeTargetEvent e) {
		playBus.fire(new ForgeZLivingChangeTarget(e), ZLivingChangeTarget.class);
	}

	/*public void sleepingLocationCheck(SleepingLocationCheckEvent e) {
		playBus.fire(new ForgeZSleepingLocationCheck(e), ZSleepingLocationCheck.class);
	}*/

	public void entityItemPickup(ItemEntityPickupEvent e) {
		playBus.fire(new ForgeZEntityItemPickup(e), ZItemEntityPickup.class);
	}

	public void blockBreak(BlockEvent.BreakEvent e) {
		playBus.fire(new ForgeZBlock.Break(e), ZBlock.Break.class);
	}

	public void blockEntityPlace(BlockEvent.EntityPlaceEvent e) {
		playBus.fire(new ForgeZBlock.EntityPlace(e), ZBlock.EntityPlace.class);
	}

	/*public void blockToolModification(BlockEvent.BlockToolModificationEvent e) {
		playBus.fire(new ForgeZBlock.BlockToolModification(e), ZBlock.BlockToolModification.class);
	}*/

	public void animalTame(AnimalTameEvent e) {
		playBus.fire(new ForgeZAnimalTame(e), ZAnimalTame.class);
	}

	public void villagerTrades(VillagerTradesEvent e) {
		playBus.fire(new ForgeZVillagerTrades(e), ZVillagerTrades.class);
	}

	public void anvilRepair(AnvilRepairEvent e) {
		playBus.fire(new ForgeZAnvilRepair(e), ZAnvilRepair.class);
	}

	public void player(PlayerEvent e) {
		playBus.fire(new ForgeZPlayer(e), ZPlayer.class);
	}

	public void playerBreakSpeed(PlayerEvent.BreakSpeed e) {
		playBus.fire(new ForgeZPlayer.BreakSpeed(e), ZPlayer.BreakSpeed.class);
	}

	public void playerClone(PlayerEvent.Clone e) {
		playBus.fire(new ForgeZPlayer.Clone(e), ZPlayer.Clone.class);
	}

	public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
		playBus.fire(new ForgeZPlayer.LoggedIn(e), ZPlayer.LoggedIn.class);
	}

	public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
		playBus.fire(new ForgeZPlayer.LoggedOut(e), ZPlayer.LoggedOut.class);
	}

	public void bonemeal(BonemealEvent e) {
		playBus.fire(new ForgeZBonemeal(e), ZBonemeal.class);
	}

	public void entityTeleport(EntityTeleportEvent e) {
		playBus.fire(new ForgeZEntityTeleport(e), ZEntityTeleport.class);
	}

	public void livingFall(LivingFallEvent e) {
		playBus.fire(new ForgeZLivingFall(e), ZLivingFall.class);
	}

	public void wandererTrades(WandererTradesEvent e) {
		playBus.fire(new ForgeZWandererTrades(e), ZWandererTrades.class);
	}

	public void furnaceFuelBurnTime(FurnaceFuelBurnTimeEvent e) {
		playBus.fire(new ForgeZFurnaceFuelBurnTime(e), ZFurnaceFuelBurnTime.class);
	}

	public void itemTooltip(ItemTooltipEvent e) {
		playBus.fire(new ForgeZItemTooltip(e), ZItemTooltip.class);
	}

	/*public static ZResult from(Event.Result r) {
		return switch(r) {
			case DENY -> ZResult.DENY;
			case DEFAULT -> ZResult.DEFAULT;
			case ALLOW -> ZResult.ALLOW;
		};
	}

	public static Event.Result to(ZResult r) {
		return switch(r) {
			case DENY -> Event.Result.DENY;
			case DEFAULT -> Event.Result.DEFAULT;
			case ALLOW -> Event.Result.ALLOW;
		};
	}*/
}
