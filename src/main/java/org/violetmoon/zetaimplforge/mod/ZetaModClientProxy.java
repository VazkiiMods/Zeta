package org.violetmoon.zetaimplforge.mod;

import net.minecraft.client.DeltaTracker;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.client.event.load.*;
import org.violetmoon.zeta.client.event.play.*;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.ZItemTooltip;
import org.violetmoon.zeta.item.IZetaItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zetaimplforge.client.event.load.*;
import org.violetmoon.zetaimplforge.client.event.play.*;
import org.violetmoon.zetaimplforge.event.ForgeEventsRemapper;
import org.violetmoon.zetaimplforge.event.load.ForgeZFirstClientTick;
import org.violetmoon.zetaimplforge.event.load.ForgeZRegisterClientReloadListener;
import org.violetmoon.zetaimplforge.event.play.ForgeZScreenshot;

public class ZetaModClientProxy extends ZetaModCommonProxy {
    @Override
    public void registerEvents(Zeta zeta){
        super.registerEvents(zeta);
        //zeta.playBus.subscribe(ClientTicker.INSTANCE).subscribe(this);

        NeoForge.EVENT_BUS.register(this);
    }

    //TODO: move these 2 events to a common class

    // added once per zeta. Its fine as we then fire it on zeta load bos which is one per zeta too.
    boolean clientTicked = false;

    @SubscribeEvent
    public void clientTick(ClientTickEvent.Pre e) {
        if (!clientTicked) {
            ZetaList.INSTANCE.fireLoadEvent(new ForgeZFirstClientTick());
            clientTicked = true;
        }
    }

    @PlayEvent
    public void onTooltip(ZItemTooltip event) {
        Item item = event.getItemStack().getItem();

        ZetaModule module = null;
        if (item instanceof IZetaItem zi) {
            module = zi.getModule();
        }
        if (item instanceof BlockItem bi && bi.getBlock() instanceof IZetaBlock zb) {
            zb.getModule();
        }

        if (module != null && !module.isEnabled() && module.category().isAddon()) {
            event.getToolTip().add(module.category().getDisabledTooltip());
        }
    }

    @Override
    public void addKnownZetaLoadEvents(ForgeEventsRemapper<IZetaLoadEvent, Event> r) {
        super.addKnownZetaLoadEvents(r);

        r.registerWrapper(ZAddModels.class, ForgeZAddModels.class);
        r.registerWrapper(ZAddModelLayers.class, ForgeZAddModelLayers.class);
        r.registerWrapper(ZClientSetup.class, ForgeZClientSetup.class);
        r.registerWrapper(ZKeyMapping.class, ForgeZKeyMapping.class);
        r.registerWrapper(ZModel.RegisterGeometryLoaders.class, ForgeZModel.RegisterGeometryLoaders.class);
        r.registerWrapper(ZModel.RegisterAdditional.class, ForgeZModel.RegisterAdditional.class);
        r.registerWrapper(ZModel.BakingCompleted.class, ForgeZModel.BakingCompleted.class);
        r.registerWrapper(ZModel.ModifyBakingResult.class, ForgeZModel.ModifyBakingResult.class);
        r.registerWrapper(ZRegisterClientExtension.class, ForgeZRegisterClientExtension.class);
        r.registerWrapper(ZRegisterLayerDefinitions.class, ForgeZRegisterLayerDefinitions.class);
        r.registerWrapper(ZTooltipComponents.class, ForgeZTooltipComponents.class);
        r.registerWrapper(ZRegisterClientReloadListener.class, ForgeZRegisterClientReloadListener.class);
        r.registerWrapper(ZFirstClientTick.class, ForgeZFirstClientTick.class);

        r.registerWrapper(ZAddBlockColorHandlers.class, ForgeZAddBlockColorHandlers.class);
        r.registerWrapper(ZAddItemColorHandlers.class, ForgeZAddItemColorHandlers.class);
    }

    @Override
    public void addKnownZetaPlayEvents(ForgeEventsRemapper<IZetaPlayEvent, Event> r) {
        super.addKnownZetaPlayEvents(r);

        r.registerWrapper(ZClientTick.End.class, ClientTickEvent.Post.class,
                ForgeZClientTick.End::new, w -> w.e);
        r.registerWrapper(ZClientTick.Start.class, ClientTickEvent.Pre.class,
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
        r.registerWrapper(ZRenderGuiOverlay.ArmorLevel.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.ArmorLevel.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ArmorLevel.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.ArmorLevel.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Crosshair.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.Crosshair.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Crosshair.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.Crosshair.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.DebugText.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.DebugText.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.DebugText.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.DebugText.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Hotbar.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.Hotbar.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.Hotbar.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.Hotbar.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PlayerHealth.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.PlayerHealth.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PlayerHealth.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.PlayerHealth.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PotionIcons.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.PotionIcons.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.PotionIcons.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.PotionIcons.Post::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ChatPanel.Pre.class, RenderGuiLayerEvent.Pre.class,
                ForgeZRenderGuiOverlay.ChatPanel.Pre::new, w -> w.e);
        r.registerWrapper(ZRenderGuiOverlay.ChatPanel.Post.class, RenderGuiLayerEvent.Post.class,
                ForgeZRenderGuiOverlay.ChatPanel.Post::new, w -> w.e);
        // zeta own event
        r.registerWrapper(ZScreenshot.class, ForgeZScreenshot.class);
        r.registerWrapper(ZEarlyRender.class, ForgeZEarlyRender.class);
    }
}
