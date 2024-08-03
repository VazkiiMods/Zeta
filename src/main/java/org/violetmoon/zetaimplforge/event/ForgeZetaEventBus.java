package org.violetmoon.zetaimplforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent;
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
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.event.load.*;
import org.violetmoon.zeta.client.event.play.*;
import org.violetmoon.zeta.event.bus.*;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.*;
import org.violetmoon.zetaimplforge.api.ForgeZGatherAdvancementModifiers;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.client.event.load.*;
import org.violetmoon.zetaimplforge.client.event.play.*;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.*;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

// this is super jank. Basically converts all zeta events to forge ones, then delegates to the forge bus directly
public class ForgeZetaEventBus<Z, F extends Event> extends ZetaEventBus<Z> {

    // needed so we can unregister later
    private final Map<ForgeZetaEventBus.Key, Object> convertedHandlers = new Object2ObjectOpenHashMap<>();

    private final IEventBus forgeBus;
    private final Class<F> forgeEventRoot; //probably not needed can be replaced with Event
    private final ForgeEventsRemapper<Z, F> remapper;

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    protected ForgeZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<Z> eventRoot,
                                @Nullable Logger logSpam, IEventBus forgeBus, Class<F> forgeEventRoot,
                                Zeta ofZeta, ForgeEventsRemapper<Z, F> remapper) {
        super(subscriberAnnotation, eventRoot, logSpam, ofZeta);
        this.forgeBus = forgeBus;
        this.forgeEventRoot = forgeEventRoot;
        this.remapper = remapper;
    }

    public static ForgeZetaEventBus<IZetaLoadEvent, Event> ofLoadBus(@Nullable Logger logSpam, Zeta ofZeta) {
        return new ForgeZetaEventBus<>(
                LoadEvent.class, IZetaLoadEvent.class,
                logSpam, FMLJavaModLoadingContext.get().getModEventBus(), Event.class,
                ofZeta, LOAD_EVENTS_REMAPPER);
    }

    public static ForgeZetaEventBus<IZetaPlayEvent, Event> ofPlayBus(@Nullable Logger logSpam, Zeta ofZeta) {
        return new ForgeZetaEventBus<>(
                PlayEvent.class, IZetaPlayEvent.class,
                logSpam, MinecraftForge.EVENT_BUS, Event.class,
                ofZeta, PLAY_EVENTS_REMAPPER);
    }


    @Override
    protected void subscribeMethod(Method method, Object receiver, Class<?> owningClazz) {
        if (method.getParameterCount() != 1)
            throw arityERR(method);

        Class<?> zetaEventClass = method.getParameterTypes()[0];

        //check if it's already a forge event, or it's a zeta event
        if (!eventRoot.isAssignableFrom(zetaEventClass) && !forgeEventRoot.isAssignableFrom(zetaEventClass))
            throw typeERR(method);

        MethodHandle handle;
        try {
            handle = MethodHandles.publicLookup().unreflect(method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //fill in the "this" parameter
        if (receiver != null)
            handle = handle.bindTo(receiver);

        Object convertedMethod = remapper.remapAndRegister(forgeBus, owningClazz, handle, zetaEventClass);
        //store here so we can unregister later
        convertedHandlers.put(new Key(method, receiver, owningClazz), convertedMethod);
    }

    @Override
    protected void unsubscribeMethod(Method m, Object receiver, Class<?> owningClazz) {
        var handler = convertedHandlers.remove(new Key(m, receiver, owningClazz));
        if (handler != null) {
            forgeBus.unregister(handler);
        }
    }

    private record Key(Method method, Object receiver, Class<?> owningClazz) {
    }

    @Override
    public <T extends Z> T fire(@NotNull T event) {
        forgeBus.post(remapper.remapEvent(event, event.getClass()));
        return event;
    }

    @Override
    public <T extends Z> T fire(@NotNull T event, Class<? super T> firedAs) {
        forgeBus.post(remapper.remapEvent(event, firedAs));
        return event;
    }

    // all these are for bus specific events. The only bus that should have specific stuff is the play one

    // for generic events
    public <S extends Z, C extends Z> void registerWrapperWithGenerics(Class<C> baseZetaEventClass, Class<S> forgeZetaEventClass, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerWrapperWithGeneric(baseZetaEventClass, forgeZetaEventClass, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass) {
        synchronized (remapper) {
            remapper.registerWrapper(baseZetaEventClass, forgeZetaEventClass, null);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapperWithGenerics(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                                         Function<? extends F, ZF> constructor, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerWrapperWithGeneric(baseZetaEventClass, forgeZetaEventClass, constructor, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                             @Nullable Function<? extends F, ZF> constructor) {
        synchronized (remapper) {
            remapper.registerWrapper(baseZetaEventClass, forgeZetaEventClass, constructor);
        }
    }

    // I would love to put this code in the mod proxy but that needs to do event setup stuff which requires these busses to be fully initialized

    // instances so we don't create multiple as reflections take time and memory
    private static final ForgeEventsRemapper<IZetaLoadEvent, Event> LOAD_EVENTS_REMAPPER = Util.make(new ForgeEventsRemapper<>(IZetaLoadEvent.class, Event.class), r -> {

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

        // client ones again?
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

        r.registerWrapper(ZAddModels.class, ForgeZAddModels.class);
        r.registerWrapper(ZAddModelLayers.class, ForgeZAddModelLayers.class);
        r.registerWrapper(ZClientSetup.class, ForgeZClientSetup.class);
        r.registerWrapper(ZKeyMapping.class, ForgeZKeyMapping.class);
        r.registerWrapper(ZModel.RegisterGeometryLoaders.class, ForgeZModel.RegisterGeometryLoaders.class);
        r.registerWrapper(ZModel.RegisterAdditional.class, ForgeZModel.RegisterAdditional.class);
        r.registerWrapper(ZModel.BakingCompleted.class, ForgeZModel.BakingCompleted.class);
        r.registerWrapper(ZModel.ModifyBakingResult.class, ForgeZModel.ModifyBakingResult.class);
        r.registerWrapper(ZRegisterLayerDefinitions.class, ForgeZRegisterLayerDefinitions.class);
        r.registerWrapper(ZTooltipComponents.class, ForgeZTooltipComponents.class);
        r.registerWrapper(ZRegisterClientReloadListener.class, ForgeZRegisterClientReloadListener.class);
        r.registerWrapper(ZFirstClientTick.class, ForgeZFirstClientTick.class);

        r.registerWrapper(ZAddBlockColorHandlers.class, ForgeZAddBlockColorHandlers.class);
        r.registerWrapper(ZAddItemColorHandlers.class, ForgeZAddItemColorHandlers.class);
    });

    private static final ForgeEventsRemapper<IZetaPlayEvent, Event> PLAY_EVENTS_REMAPPER = Util.make(new ForgeEventsRemapper<>(IZetaPlayEvent.class, Event.class), r -> {

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

        r.registerWrapper(ZRecipeCrawl.class, ForgeZRecipeCrawl.Digest.class,
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

        //Hmm client events here? maybe i should move them
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

        r.registerWrapper(ZClientTick.End.class, TickEvent.ClientTickEvent.class,
                ForgeZClientTick.End::new, w -> w.e);
        r.registerWrapper(ZClientTick.Start.class, TickEvent.ClientTickEvent.class,
                ForgeZClientTick.Start::new, w -> w.e);
        r.registerWrapper(ZGatherTooltipComponents.class, ForgeZGatherTooltipComponents.class);
        r.registerWrapper(ZHighlightBlock.class, ForgeZHighlightBlock.class);
        r.registerWrapper(ZInput.MouseButton.class, ForgeZInput.MouseButton.class);
        r.registerWrapper(ZInput.Key.class, ForgeZInput.Key.class);
        r.registerWrapper(ZInputUpdate.class, ForgeZInputUpdate.class);
        r.registerWrapper(ZRenderContainerScreen.Background.class, ForgeZRenderContainerScreen.Background.class);
        r.registerWrapper(ZRenderContainerScreen.Foreground.class, ForgeZRenderContainerScreen.Foreground.class);
        r.registerWrapper(ZRenderLiving.PostLowest.class, RenderLivingEvent.Post.class,
                ForgeZRenderLiving.PostLowest::new, w -> w.e);
        r.registerWrapper(ZRenderLiving.PreHighest.class, RenderLivingEvent.Pre.class,
                ForgeZRenderLiving.PreHighest::new, w -> w.e);
        r.registerWrapper(ZRenderPlayer.Post.class, RenderPlayerEvent.Post.class,
                ForgeZRenderPlayer.Post::new, w -> w.e);
        r.registerWrapper(ZRenderPlayer.Pre.class, RenderPlayerEvent.Pre.class,
                ForgeZRenderPlayer.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderTick.End.class, TickEvent.RenderTickEvent.class,
                ForgeZRenderTick.End::new, w -> w.e);
        r.registerWrapper(ZRenderTick.Start.class, TickEvent.RenderTickEvent.class,
                ForgeZRenderTick.Start::new, w -> w.e);
        r.registerWrapper(ZRenderTooltip.GatherComponents.class, ForgeZRenderTooltip.GatherComponents.class);
        r.registerWrapper(ZRenderTooltip.GatherComponents.Low.class, ForgeZRenderTooltip.GatherComponents.Low.class);
        r.registerWrapper(ZScreen.Opening.class, ForgeZScreen.Opening.class);
        r.registerWrapper(ZScreen.CharacterTyped.Pre.class, ForgeZScreen.CharacterTyped.Pre.class);
        r.registerWrapper(ZScreen.CharacterTyped.Post.class, ForgeZScreen.CharacterTyped.Post.class);
        r.registerWrapper(ZScreen.Init.Post.class, ForgeZScreen.Init.Post.class);
        r.registerWrapper(ZScreen.Init.Pre.class, ForgeZScreen.Init.Pre.class);
        r.registerWrapper(ZScreen.KeyPressed.Post.class, ForgeZScreen.KeyPressed.Post.class);
        r.registerWrapper(ZScreen.KeyPressed.Pre.class, ForgeZScreen.KeyPressed.Pre.class);
        r.registerWrapper(ZScreen.MouseScrolled.Post.class, ForgeZScreen.MouseScrolled.Post.class);
        r.registerWrapper(ZScreen.MouseScrolled.Pre.class, ForgeZScreen.MouseScrolled.Pre.class);
        r.registerWrapper(ZScreen.MouseButtonPressed.Post.class, ForgeZScreen.MouseButtonPressed.Post.class);
        r.registerWrapper(ZScreen.MouseButtonPressed.Pre.class, ForgeZScreen.MouseButtonPressed.Pre.class);
        r.registerWrapper(ZScreen.Render.Post.class, ScreenEvent.Render.Post.class,
                ForgeZScreen.Render.Post::new, w -> w.e);
        r.registerWrapper(ZScreen.Render.Pre.class, ScreenEvent.Render.Pre.class,
                ForgeZScreen.Render.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ArmorLevel.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.ArmorLevel.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ArmorLevel.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.ArmorLevel.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Crosshair.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.Crosshair.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Crosshair.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.Crosshair.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.DebugText.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.DebugText.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.DebugText.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.DebugText.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Hotbar.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.Hotbar.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Hotbar.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.Hotbar.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PlayerHealth.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.PlayerHealth.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PlayerHealth.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.PlayerHealth.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PotionIcons.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.PotionIcons.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PotionIcons.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.PotionIcons.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ChatPanel.Pre.class, RenderGuiOverlayEvent.Pre.class,
                ForgeZRenderGuiOverlay.ChatPanel.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ChatPanel.Post.class, RenderGuiOverlayEvent.Post.class,
                ForgeZRenderGuiOverlay.ChatPanel.Post::new, w -> w.e);
        // zeta own event
        r.registerWrapper(ZScreenshot.class, ForgeZScreenshot.class);
        r.registerWrapper(ZEarlyRender.class, ForgeZEarlyRender.class);

    });


}
