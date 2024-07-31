package org.violetmoon.zetaimplforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
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
import java.util.function.Consumer;
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

        Consumer<? extends F> consumer = remapper.remapMethod(handle, zetaEventClass);
        remapper.registerListenerToForgeWithPriorityAndGenerics(forgeBus, owningClazz, consumer, zetaEventClass);
        //store here so we can unregister later
        convertedHandlers.put(new Key(method, receiver, owningClazz), consumer);
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
    public <S extends Z, C extends Z> void registerSubClassWithGeneric(Class<C> baseZetaEventClass, Class<S> forgeZetaEventClass, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerSubClassWithGeneric(baseZetaEventClass, forgeZetaEventClass, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerSubClass(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass) {
        synchronized (remapper) {
            remapper.registerSubClass(baseZetaEventClass, forgeZetaEventClass, null);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerSubClassWithGeneric(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                                         Function<? extends F, ZF> constructor, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerSubClassWithGeneric(baseZetaEventClass, forgeZetaEventClass, constructor, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerSubClass(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                              @Nullable Function<? extends F, ZF> constructor) {
        synchronized (remapper) {
            remapper.registerSubClass(baseZetaEventClass, forgeZetaEventClass, constructor);
        }
    }

    // I would love to put this code in the mod proxy but that needs to do event setup stuff which requires these busses to be fully initialized

    // instances so we don't create multiple as reflections take time and memory
    private static final ForgeEventsRemapper<IZetaLoadEvent, Event> LOAD_EVENTS_REMAPPER = Util.make(new ForgeEventsRemapper<>(IZetaLoadEvent.class, Event.class), r -> {

        // adds known events subclasses to the bus
        r.registerSubClass(ZCommonSetup.class, ForgeZCommonSetup.class);
        r.registerSubClass(ZEntityAttributeCreation.class, ForgeZEntityAttributeCreation.class);
        r.registerSubClass(ZModulesReady.class, ForgeZModulesReady.class);
        r.registerSubClass(ZRegister.class, ForgeZRegister.class);
        r.registerSubClass(ZRegister.Post.class, ForgeZRegister.Post.class);
        r.registerSubClass(ZConfigChanged.class, ForgeZConfigChange.class);
        r.registerSubClass(ZLoadComplete.class, ForgeZLoadComplete.class);

        // client ones again?
        if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

        r.registerSubClass(ZAddModels.class, ForgeZAddModels.class);
        r.registerSubClass(ZAddModelLayers.class, ForgeZAddModelLayers.class);
        r.registerSubClass(ZClientSetup.class, ForgeZClientSetup.class);
        r.registerSubClass(ZKeyMapping.class, ForgeZKeyMapping.class);
        r.registerSubClass(ZModel.RegisterGeometryLoaders.class, ForgeZModel.RegisterGeometryLoaders.class);
        r.registerSubClass(ZModel.RegisterAdditional.class, ForgeZModel.RegisterAdditional.class);
        r.registerSubClass(ZModel.BakingCompleted.class, ForgeZModel.BakingCompleted.class);
        r.registerSubClass(ZModel.ModifyBakingResult.class, ForgeZModel.ModifyBakingResult.class);
        r.registerSubClass(ZRegisterLayerDefinitions.class, ForgeZRegisterLayerDefinitions.class);
        r.registerSubClass(ZTooltipComponents.class, ForgeZTooltipComponents.class);
        r.registerSubClass(ZRegisterClientReloadListener.class, ForgeZRegisterClientReloadListener.class);
        r.registerSubClass(ZFirstClientTick.class, ForgeZFirstClientTick.class);

        r.registerSubClass(ZAddBlockColorHandlers.class, ForgeZAddBlockColorHandlers.class);
        r.registerSubClass(ZAddItemColorHandlers.class, ForgeZAddItemColorHandlers.class);
    });

    private static final ForgeEventsRemapper<IZetaPlayEvent, Event> PLAY_EVENTS_REMAPPER = Util.make(new ForgeEventsRemapper<>(IZetaPlayEvent.class, Event.class), r -> {

        r.registerSubClass(ZAnvilRepair.class, ForgeZAnvilRepair.class);
        r.registerSubClass(ZAnvilUpdate.Highest.class, ForgeZAnvilUpdate.Highest.class);
        r.registerSubClass(ZAnvilUpdate.Lowest.class, ForgeZAnvilUpdate.Lowest.class);
        r.registerSubClass(ZTagsUpdated.class, ForgeZTagsUpdated.class);
        r.registerSubClass(ZBabyEntitySpawn.Lowest.class, ForgeZBabyEntitySpawn.Lowest.class);
        r.registerSubClass(ZBabyEntitySpawn.class, ForgeZBabyEntitySpawn.class);
        r.registerSubClass(ZBlock.Break.class, ForgeZBlock.Break.class);
        r.registerSubClass(ZBlock.EntityPlace.class, ForgeZBlock.EntityPlace.class);
        r.registerSubClass(ZBlock.BlockToolModification.class, ForgeZBlock.BlockToolModification.class);
        r.registerSubClass(ZBonemeal.class, ForgeZBonemeal.class);
        r.registerSubClass(ZEntityConstruct.class, ForgeZEntityConstruct.class);
        r.registerSubClass(ZEntityInteract.class, ForgeZEntityInteract.class);
        r.registerSubClass(ZEntityItemPickup.class, ForgeZEntityItemPickup.class);
        r.registerSubClass(ZEntityJoinLevel.class, ForgeZEntityJoinLevel.class);
        r.registerSubClass(ZEntityMobGriefing.class, ForgeZEntityMobGriefing.class);
        r.registerSubClass(ZEntityTeleport.class, ForgeZEntityTeleport.class);
        r.registerSubClass(ZItemTooltip.class, ForgeZItemTooltip.class);
        r.registerSubClass(ZLivingChangeTarget.class, ForgeZLivingChangeTarget.class);
        r.registerSubClass(ZLivingConversion.class, ForgeZLivingConversion.class);
        r.registerSubClass(ZLivingConversion.Pre.class, ForgeZLivingConversion.Pre.class);
        r.registerSubClass(ZLivingConversion.Post.class, ForgeZLivingConversion.Post.class);
        r.registerSubClass(ZLivingDeath.class, ForgeZLivingDeath.class);
        r.registerSubClass(ZLivingDeath.Lowest.class, ForgeZLivingDeath.Lowest.class);
        r.registerSubClass(ZLivingDrops.class, ForgeZLivingDrops.class);
        r.registerSubClass(ZLivingDrops.Lowest.class, ForgeZLivingDrops.Lowest.class);
        r.registerSubClass(ZLivingFall.class, ForgeZLivingFall.class);
        r.registerSubClass(ZLivingTick.class, ForgeZLivingTick.class);
        r.registerSubClass(ZMobSpawnEvent.class, ForgeZMobSpawnEvent.class);
        r.registerSubClass(ZMobSpawnEvent.CheckSpawn.class, ForgeZMobSpawnEvent.FinalizeSpawn.class);
        r.registerSubClass(ZMobSpawnEvent.CheckSpawn.Lowest.class, ForgeZMobSpawnEvent.FinalizeSpawn.Lowest.class);
        r.registerSubClass(ZPlayNoteBlock.class, ForgeZPlayNoteBlock.class);
        r.registerSubClass(ZPlayer.class, ForgeZPlayer.class);
        r.registerSubClass(ZPlayer.BreakSpeed.class, ForgeZPlayer.BreakSpeed.class);
        r.registerSubClass(ZPlayer.Clone.class, ForgeZPlayer.Clone.class);
        r.registerSubClass(ZPlayerDestroyItem.class, ForgeZPlayerDestroyItem.class);
        r.registerSubClass(ZPlayer.LoggedIn.class, ForgeZPlayer.LoggedIn.class);
        r.registerSubClass(ZPlayer.LoggedOut.class, ForgeZPlayer.LoggedOut.class);
        r.registerSubClass(ZPlayerTick.Start.class, ForgeZPlayerTick.Start.class);
        r.registerSubClass(ZPlayerTick.End.class, ForgeZPlayerTick.End.class);
        r.registerSubClass(ZPlayerInteract.class, ForgeZPlayerInteract.class);
        r.registerSubClass(ZPlayerInteract.EntityInteractSpecific.class, ForgeZPlayerInteract.EntityInteractSpecific.class);
        r.registerSubClass(ZPlayerInteract.EntityInteract.class, ForgeZPlayerInteract.EntityInteract.class);
        r.registerSubClass(ZPlayerInteract.RightClickBlock.class, ForgeZPlayerInteract.RightClickBlock.class);
        r.registerSubClass(ZPlayerInteract.RightClickItem.class, ForgeZPlayerInteract.RightClickItem.class);
        r.registerSubClass(ZRightClickBlock.class, ForgeZRightClickBlock.class);
        r.registerSubClass(ZRightClickBlock.Low.class, ForgeZRightClickBlock.Low.class);
        r.registerSubClass(ZRightClickItem.class, ForgeZRightClickItem.class);
        r.registerSubClass(ZLootTableLoad.class, ForgeZLootTableLoad.class);
        r.registerSubClass(ZVillagerTrades.class, ForgeZVillagerTrades.class);
        r.registerSubClass(ZWandererTrades.class, ForgeZWandererTrades.class);
        r.registerSubClass(ZFurnaceFuelBurnTime.class, ForgeZFurnaceFuelBurnTime.class);
        r.registerSubClass(ZGatherAdditionalFlags.class, ForgeZGatherAdditionalFlags.class);
        r.registerSubClass(ZServerTick.Start.class, ForgeZServerTick.Start.class);
        r.registerSubClass(ZServerTick.End.class, ForgeZServerTick.End.class);
        r.registerSubClass(ZAddReloadListener.class, ForgeZAddReloadListener.class);
        r.registerSubClass(ZGatherAdvancementModifiers.class, ForgeZGatherAdvancementModifiers.class);
        r.registerSubClass(ZGatherHints.class, ForgeZGatherHints.class);
        r.registerSubClass(ZSleepingLocationCheck.class, ForgeZSleepingLocationCheck.class);
        r.registerSubClass(ZAnimalTame.class, ForgeZAnimalTame.class);
        r.registerSubClass(ZLevelTick.End.class, ForgeZLevelTick.End.class);
        r.registerSubClass(ZLevelTick.Start.class, ForgeZLevelTick.Start.class);



        //this is ugly. generic events here
        r.registerSubClassWithGeneric(ZAttachCapabilities.BlockEntityCaps.class,
                ForgeZAttachCapabilities.BlockEntityCaps.class,
                (Function<AttachCapabilitiesEvent<BlockEntity>, ForgeZAttachCapabilities.BlockEntityCaps>) inner ->
                        new ForgeZAttachCapabilities.BlockEntityCaps(ForgeCapabilityManager.INSTANCE, inner),
                BlockEntity.class);
        r.registerSubClassWithGeneric(ZAttachCapabilities.ItemStackCaps.class,
                ForgeZAttachCapabilities.ItemStackCaps.class,
                (Function<AttachCapabilitiesEvent<ItemStack>, ForgeZAttachCapabilities.ItemStackCaps>) inner ->
                        new ForgeZAttachCapabilities.ItemStackCaps(ForgeCapabilityManager.INSTANCE, inner),
                ItemStack.class);
        r.registerSubClassWithGeneric(ZAttachCapabilities.LevelCaps.class,
                ForgeZAttachCapabilities.LevelCaps.class,
                (Function<AttachCapabilitiesEvent<Level>, ForgeZAttachCapabilities.LevelCaps>) inner ->
                        new ForgeZAttachCapabilities.LevelCaps(ForgeCapabilityManager.INSTANCE, inner),
                Level.class);

        // zeta specific ones

        r.registerSubClass(ZRecipeCrawl.Digest.class, ForgeZRecipeCrawl.Digest.class);
        r.registerSubClass(ZRecipeCrawl.Reset.class, ForgeZRecipeCrawl.Reset.class);
        r.registerSubClass(ZRecipeCrawl.Starting.class, ForgeZRecipeCrawl.Starting.class);
        r.registerSubClass(ZRecipeCrawl.Visit.Cooking.class, ForgeZRecipeCrawl.Visit.Cooking.class);
        r.registerSubClass(ZRecipeCrawl.Visit.Custom.class, ForgeZRecipeCrawl.Visit.Custom.class);
        r.registerSubClass(ZRecipeCrawl.Visit.Misc.class, ForgeZRecipeCrawl.Visit.Misc.class);
        r.registerSubClass(ZRecipeCrawl.Visit.Shaped.class, ForgeZRecipeCrawl.Visit.Shaped.class);
        r.registerSubClass(ZRecipeCrawl.Visit.Shapeless.class, ForgeZRecipeCrawl.Visit.Shapeless.class);


        //Hmm client events here? maybe i should move them
        if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

        r.registerSubClass(ZClientTick.End.class, ForgeZClientTick.End.class);
        r.registerSubClass(ZClientTick.Start.class, ForgeZClientTick.Start.class);
        r.registerSubClass(ZEarlyRender.class, ForgeZEarlyRender.class);
        r.registerSubClass(ZGatherTooltipComponents.class, ForgeZGatherTooltipComponents.class);
        r.registerSubClass(ZHighlightBlock.class, ForgeZHighlightBlock.class);
        r.registerSubClass(ZInput.MouseButton.class, ForgeZInput.MouseButton.class);
        r.registerSubClass(ZInput.Key.class, ForgeZInput.Key.class);
        r.registerSubClass(ZInputUpdate.class, ForgeZInputUpdate.class);
        r.registerSubClass(ZRenderContainerScreen.Background.class, ForgeZRenderContainerScreen.Background.class);
        r.registerSubClass(ZRenderContainerScreen.Foreground.class, ForgeZRenderContainerScreen.Foreground.class);
        r.registerSubClass(ZRenderLiving.PostLowest.class, ForgeZRenderLiving.PostLowest.class);
        r.registerSubClass(ZRenderLiving.PreHighest.class, ForgeZRenderLiving.PreHighest.class);
        r.registerSubClass(ZRenderPlayer.Post.class, ForgeZRenderPlayer.Post.class);
        r.registerSubClass(ZRenderPlayer.Pre.class, ForgeZRenderPlayer.Pre.class);
        r.registerSubClass(ZRenderTick.class, ForgeZRenderTick.class);
        r.registerSubClass(ZRenderTooltip.GatherComponents.class, ForgeZRenderTooltip.GatherComponents.class);
        r.registerSubClass(ZRenderTooltip.GatherComponents.Low.class, ForgeZRenderTooltip.GatherComponents.Low.class);
        r.registerSubClass(ZScreen.Opening.class, ForgeZScreen.Opening.class);
        r.registerSubClass(ZScreen.CharacterTyped.Pre.class, ForgeZScreen.CharacterTyped.Pre.class);
        r.registerSubClass(ZScreen.CharacterTyped.Post.class, ForgeZScreen.CharacterTyped.Post.class);
        r.registerSubClass(ZScreen.Init.Post.class, ForgeZScreen.Init.Post.class);
        r.registerSubClass(ZScreen.Init.Pre.class, ForgeZScreen.Init.Pre.class);
        r.registerSubClass(ZScreen.KeyPressed.Post.class, ForgeZScreen.KeyPressed.Post.class);
        r.registerSubClass(ZScreen.KeyPressed.Pre.class, ForgeZScreen.KeyPressed.Pre.class);
        r.registerSubClass(ZScreen.MouseScrolled.Post.class, ForgeZScreen.MouseScrolled.Post.class);
        r.registerSubClass(ZScreen.MouseScrolled.Pre.class, ForgeZScreen.MouseScrolled.Pre.class);
        r.registerSubClass(ZScreen.MouseButtonPressed.Post.class, ForgeZScreen.MouseButtonPressed.Post.class);
        r.registerSubClass(ZScreen.MouseButtonPressed.Pre.class, ForgeZScreen.MouseButtonPressed.Pre.class);
        r.registerSubClass(ZScreen.Render.Post.class, ForgeZScreen.Render.Post.class);
        r.registerSubClass(ZScreen.Render.Pre.class, ForgeZScreen.Render.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.ArmorLevel.Pre.class, ForgeZRenderGuiOverlay.ArmorLevel.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.ArmorLevel.Post.class, ForgeZRenderGuiOverlay.ArmorLevel.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.Crosshair.Post.class, ForgeZRenderGuiOverlay.Crosshair.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.Crosshair.Pre.class, ForgeZRenderGuiOverlay.Crosshair.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.DebugText.Pre.class, ForgeZRenderGuiOverlay.DebugText.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.DebugText.Post.class, ForgeZRenderGuiOverlay.DebugText.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.Hotbar.Pre.class, ForgeZRenderGuiOverlay.Hotbar.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.Hotbar.Post.class, ForgeZRenderGuiOverlay.Hotbar.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.PlayerHealth.Pre.class, ForgeZRenderGuiOverlay.PlayerHealth.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.PlayerHealth.Post.class, ForgeZRenderGuiOverlay.PlayerHealth.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.PotionIcons.Pre.class, ForgeZRenderGuiOverlay.PotionIcons.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.PotionIcons.Post.class, ForgeZRenderGuiOverlay.PotionIcons.Post.class);
        r.registerSubClass(ZRenderGuiOverlay.ChatPanel.Pre.class, ForgeZRenderGuiOverlay.ChatPanel.Pre.class);
        r.registerSubClass(ZRenderGuiOverlay.ChatPanel.Post.class, ForgeZRenderGuiOverlay.ChatPanel.Post.class);
        r.registerSubClass(ZScreenshot.class, ForgeZScreenshot.class);

    });


}
