package org.violetmoon.zetaimplforge.mod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.*;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;
import org.violetmoon.zetaimplforge.api.ForgeZGatherAdvancementModifiers;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.event.ForgeEventsRemapper;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.*;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

import java.util.function.Function;

public class ZetaModCommonProxy {

	public void registerEvents(Zeta zeta){
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::onSetup);

		zeta.loadBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(EntitySpawnHandler.class)
				.subscribe(WorldGenHandler.class)
				.subscribe(ZetaGeneralConfig.class);

		zeta.playBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(SyncedFlagHandler.class);


		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
		ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());

	}


	public void onSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(ConfigEventDispatcher::dispatchAllInitialLoads);
	}

	public void addKnownZetaLoadEvents(ForgeEventsRemapper<IZetaLoadEvent, Event> r){

		//TODO: repace all these with explicit ones

		// adds known events subclasses to the bus
		r.registerWrapper(ZCommonSetup.class, FMLCommonSetupEvent.class,
				ForgeZCommonSetup::new, ForgeZCommonSetup::e);
		r.registerWrapper(ZEntityAttributeCreation.class, EntityAttributeCreationEvent.class,
				ForgeZEntityAttributeCreation::new, ForgeZEntityAttributeCreation::e);
		r.registerWrapper(ZModulesReady.class, ForgeZModulesReady.class);
		r.registerWrapper(ZRegister.class, ForgeZRegister.class);
		r.registerWrapper(ZRegister.Post.class, ForgeZRegister.Post.class);
		r.registerWrapper(ZConfigChanged.class, ForgeZConfigChange.class);
		r.registerWrapper(ZLoadComplete.class, ForgeZLoadComplete.class);

		//zeta own
		r.registerWrapper(ZGatherAdvancementModifiers.class, ForgeZGatherAdvancementModifiers.class);

	}

	public void addKnownZetaPlayEvents(ForgeEventsRemapper<IZetaPlayEvent, Event> r){

		r.registerWrapper(ZAnvilRepair.class, ForgeZAnvilRepair.class);
		r.registerWrapper(ZAnvilUpdate.Highest.class, ForgeZAnvilUpdate.Highest.class);
		r.registerWrapper(ZAnvilUpdate.Lowest.class, ForgeZAnvilUpdate.Lowest.class);
		r.registerWrapper(ZTagsUpdated.class, ForgeZTagsUpdated.class);
		r.registerWrapper(ZBabyEntitySpawn.Lowest.class, BabyEntitySpawnEvent.class,
				ForgeZBabyEntitySpawn.Lowest::new, a -> a.wrapped);
		r.registerWrapper(ZBabyEntitySpawn.class, BabyEntitySpawnEvent.class,
				ForgeZBabyEntitySpawn::new, a -> a.wrapped);
		r.registerWrapper(ZBlock.Break.class, ForgeZBlock.Break.class);
		r.registerWrapper(ZBlock.EntityPlace.class, ForgeZBlock.EntityPlace.class);
		r.registerWrapper(ZBlock.BlockToolModification.class, ForgeZBlock.BlockToolModification.class);
		r.registerWrapper(ZBonemeal.class, ForgeZBonemeal.class);
		r.registerWrapper(ZEntityConstruct.class, ForgeZEntityConstruct.class);
		r.registerWrapper(ZEntityInteract.class, PlayerInteractEvent.EntityInteract.class,
				ForgeZEntityInteract::new, ForgeZEntityInteract::e);
		r.registerWrapper(ZEntityItemPickup.class, ForgeZEntityItemPickup.class);
		r.registerWrapper(ZEntityJoinLevel.class, ForgeZEntityJoinLevel.class);
		r.registerWrapper(ZEntityMobGriefing.class, ForgeZEntityMobGriefing.class);
		r.registerWrapper(ZEntityTeleport.class, ForgeZEntityTeleport.class);
		r.registerWrapper(ZItemTooltip.class, ItemTooltipEvent.class,
				ForgeZItemTooltip::new, ForgeZItemTooltip::e);
		r.registerWrapper(ZLivingChangeTarget.class, LivingChangeTargetEvent.class,
				ForgeZLivingChangeTarget::new, ForgeZLivingChangeTarget::e);
		r.registerWrapper(ZLivingConversion.class, ForgeZLivingConversion.class);
		r.registerWrapper(ZLivingConversion.Pre.class, ForgeZLivingConversion.Pre.class);
		r.registerWrapper(ZLivingConversion.Post.class, ForgeZLivingConversion.Post.class);
		r.registerWrapper(ZLivingDeath.class, ForgeZLivingDeath.class);
		r.registerWrapper(ZLivingDeath.Lowest.class, ForgeZLivingDeath.Lowest.class);
		r.registerWrapper(ZLivingDrops.class, LivingDropsEvent.class,
				ForgeZLivingDrops::new, w -> w.e);
		r.registerWrapper(ZLivingDrops.Lowest.class, LivingDropsEvent.class,
				ForgeZLivingDrops.Lowest::new, w -> w.e);
		r.registerWrapper(ZLivingFall.class, ForgeZLivingFall.class);
		r.registerWrapper(ZLivingTick.class, LivingEvent.LivingTickEvent.class,
				ForgeZLivingTick::new, ForgeZLivingTick::e);
		r.registerWrapper(ZMobSpawnEvent.class, MobSpawnEvent.class,
				ForgeZMobSpawnEvent::new, w -> w.e);
		r.registerWrapper(ZMobSpawnEvent.CheckSpawn.class, MobSpawnEvent.FinalizeSpawn.class,
				ForgeZMobSpawnEvent.FinalizeSpawn::new, w -> w.e);
		r.registerWrapper(ZMobSpawnEvent.CheckSpawn.Lowest.class, MobSpawnEvent.FinalizeSpawn.class,
				ForgeZMobSpawnEvent.FinalizeSpawn.Lowest::new, w -> w.e);
		r.registerWrapper(ZPlayNoteBlock.class, ForgeZPlayNoteBlock.class);
		r.registerWrapper(ZPlayer.BreakSpeed.class, ForgeZPlayer.BreakSpeed.class);
		r.registerWrapper(ZPlayer.Clone.class, ForgeZPlayer.Clone.class);
		r.registerWrapper(ZPlayerDestroyItem.class, ForgeZPlayerDestroyItem.class);
		r.registerWrapper(ZPlayer.LoggedIn.class, ForgeZPlayer.LoggedIn.class);
		r.registerWrapper(ZPlayer.LoggedOut.class, ForgeZPlayer.LoggedOut.class);
		r.registerWrapper(ZPlayerTick.Start.class, TickEvent.PlayerTickEvent.class,
				ForgeZPlayerTick.Start::new, w -> w.e);
		r.registerWrapper(ZPlayerTick.End.class, TickEvent.PlayerTickEvent.class,
				ForgeZPlayerTick.End::new, w -> w.e);
		r.registerWrapper(ZPlayerInteract.class, ForgeZPlayerInteract.class);
		r.registerWrapper(ZPlayerInteract.EntityInteractSpecific.class, ForgeZPlayerInteract.EntityInteractSpecific.class);
		r.registerWrapper(ZPlayerInteract.EntityInteract.class, ForgeZPlayerInteract.EntityInteract.class);
		r.registerWrapper(ZPlayerInteract.RightClickBlock.class, ForgeZPlayerInteract.RightClickBlock.class);
		r.registerWrapper(ZPlayerInteract.RightClickItem.class, ForgeZPlayerInteract.RightClickItem.class);
		r.registerWrapper(ZRightClickBlock.class, ForgeZRightClickBlock.class);
		r.registerWrapper(ZRightClickBlock.Low.class, ForgeZRightClickBlock.Low.class);
		r.registerWrapper(ZRightClickItem.class, ForgeZRightClickItem.class);
		r.registerWrapper(ZLootTableLoad.class, ForgeZLootTableLoad.class);
		r.registerWrapper(ZVillagerTrades.class, ForgeZVillagerTrades.class);
		r.registerWrapper(ZWandererTrades.class, ForgeZWandererTrades.class);
		r.registerWrapper(ZFurnaceFuelBurnTime.class, ForgeZFurnaceFuelBurnTime.class);
		r.registerWrapper(ZGatherAdditionalFlags.class, ForgeZGatherAdditionalFlags.class);
		r.registerWrapper(ZServerTick.Start.class, TickEvent.ServerTickEvent.class,
				ForgeZServerTick.Start::new, w -> w.e);
		r.registerWrapper(ZServerTick.End.class, TickEvent.ServerTickEvent.class,
				ForgeZServerTick.End::new, w -> w.e);
		r.registerWrapper(ZAddReloadListener.class, ForgeZAddReloadListener.class);
		r.registerWrapper(ZGatherHints.class, ForgeZGatherHints.class);
		r.registerWrapper(ZSleepingLocationCheck.class, ForgeZSleepingLocationCheck.class);
		r.registerWrapper(ZAnimalTame.class, ForgeZAnimalTame.class);
		r.registerWrapper(ZLevelTick.End.class, TickEvent.LevelTickEvent.class,
				ForgeZLevelTick.End::new, w -> w.e);
		r.registerWrapper(ZLevelTick.Start.class, TickEvent.LevelTickEvent.class,
				ForgeZLevelTick.Start::new, w -> w.e);


		//this is ugly. generic events here
		r.registerWrapperWithGeneric(ZAttachCapabilities.BlockEntityCaps.class,
				ForgeZAttachCapabilities.BlockEntityCaps.class,
				(Function<AttachCapabilitiesEvent<BlockEntity>, ForgeZAttachCapabilities.BlockEntityCaps>) inner ->
						new ForgeZAttachCapabilities.BlockEntityCaps(ForgeCapabilityManager.INSTANCE, inner),
				BlockEntity.class);
		r.registerWrapperWithGeneric(ZAttachCapabilities.ItemStackCaps.class,
				ForgeZAttachCapabilities.ItemStackCaps.class,
				(Function<AttachCapabilitiesEvent<ItemStack>, ForgeZAttachCapabilities.ItemStackCaps>) inner ->
						new ForgeZAttachCapabilities.ItemStackCaps(ForgeCapabilityManager.INSTANCE, inner),
				ItemStack.class);
		r.registerWrapperWithGeneric(ZAttachCapabilities.LevelCaps.class,
				ForgeZAttachCapabilities.LevelCaps.class,
				(Function<AttachCapabilitiesEvent<Level>, ForgeZAttachCapabilities.LevelCaps>) inner ->
						new ForgeZAttachCapabilities.LevelCaps(ForgeCapabilityManager.INSTANCE, inner),
				Level.class);

		// zeta specific ones

		r.registerWrapper(ZRecipeCrawl.Digest.class, ForgeZRecipeCrawl.Digest.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Digest::new);
		r.registerWrapper(ZRecipeCrawl.Reset.class, ForgeZRecipeCrawl.Reset.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Reset::new);
		r.registerWrapper(ZRecipeCrawl.Starting.class, ForgeZRecipeCrawl.Starting.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Starting::new);
		r.registerWrapper(ZRecipeCrawl.Visit.Cooking.class, ForgeZRecipeCrawl.Visit.Cooking.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Visit.Cooking::new);
		r.registerWrapper(ZRecipeCrawl.Visit.Custom.class, ForgeZRecipeCrawl.Visit.Custom.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Visit.Custom::new);
		r.registerWrapper(ZRecipeCrawl.Visit.Misc.class, ForgeZRecipeCrawl.Visit.Misc.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Visit.Misc::new);
		r.registerWrapper(ZRecipeCrawl.Visit.Shaped.class, ForgeZRecipeCrawl.Visit.Shaped.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Visit.Shaped::new);
		r.registerWrapper(ZRecipeCrawl.Visit.Shapeless.class, ForgeZRecipeCrawl.Visit.Shapeless.class,
				ForgeZRecipeCrawl::get, ForgeZRecipeCrawl.Visit.Shapeless::new);

	}


}
