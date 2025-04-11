package org.violetmoon.zetaimplforge.mod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.load.ZGatherAdditionalFlags;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.*;
import org.violetmoon.zeta.event.play.loading.ZGatherHints;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;
import org.violetmoon.zetaimplforge.event.ForgeEventsRemapper;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.load.ForgeZGatherAdditionalFlags;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.*;
import org.violetmoon.zetaimplforge.event.play.loading.ForgeZGatherHints;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

import java.util.function.Function;

public class ZetaModCommonProxy {

    public void registerEvents(Zeta zeta) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

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


        NeoForge.EVENT_BUS.register(ToolInteractionHandler.class);
        ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());

    }

    public void addKnownZetaLoadEvents(ForgeEventsRemapper<IZetaLoadEvent, Event> r) {

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
        r.registerWrapper(ZGatherAdditionalFlags.class, ForgeZGatherAdditionalFlags.class);
        r.registerWrapper(org.violetmoon.zeta.event.load.ZGatherHints.class, org.violetmoon.zetaimplforge.event.load.ForgeZGatherHints.class);


    }

    public void addKnownZetaPlayEvents(ForgeEventsRemapper<IZetaPlayEvent, Event> r) {

        r.registerWrapper(ZAnvilRepair.class, ForgeZAnvilRepair.class);
        r.registerWrapper(ZAnvilUpdate.class, ForgeZAnvilUpdate.class);
        r.registerWrapper(ZAnvilUpdate.Highest.class, ForgeZAnvilUpdate.Highest.class);
        r.registerWrapper(ZAnvilUpdate.Lowest.class, ForgeZAnvilUpdate.Lowest.class);
        r.registerWrapper(ZTagsUpdated.class, ForgeZTagsUpdated.class);
        r.registerWrapper(ZBabyEntitySpawn.Lowest.class, BabyEntitySpawnEvent.class,
                ForgeZBabyEntitySpawn.Lowest::new, a -> a.wrapped);
        r.registerWrapper(ZBabyEntitySpawn.class, BabyEntitySpawnEvent.class,
                ForgeZBabyEntitySpawn::new, a -> a.wrapped);
        r.registerWrapper(ZBlock.Break.class, BlockEvent.BreakEvent.class,
                ForgeZBlock.Break::new, w -> w.e);
        r.registerWrapper(ZBlock.EntityPlace.class, BlockEvent.EntityPlaceEvent.class,
                ForgeZBlock.EntityPlace::new, w -> w.e);
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

        r.registerWrapper(org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags.class,
                org.violetmoon.zetaimplforge.event.play.loading.ForgeZGatherAdditionalFlags.class);


    }


}
