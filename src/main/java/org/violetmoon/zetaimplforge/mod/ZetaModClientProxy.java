package org.violetmoon.zetaimplforge.mod;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.TopLayerTooltipHandler;
import org.violetmoon.zeta.client.event.play.ZFirstClientTick;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;
import org.violetmoon.zeta.util.handler.RequiredModTooltipHandler;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zetaimplforge.client.ForgeZetaClient;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderGuiOverlay;

public class ZetaModClientProxy extends ZetaModCommonProxy {

	private final ForgeZetaClient clientZeta;

	public ZetaModClientProxy(Zeta zeta) {
		super(zeta);
		this.clientZeta = new ForgeZetaClient(zeta);

		zeta.playBus
			.subscribe(TopLayerTooltipHandler.class)
			.subscribe(new RequiredModTooltipHandler.Client(zeta));

		MinecraftForge.EVENT_BUS.addListener(this::clientTick);

		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPre);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPost);
	}

	// added once per zeta. Its fine as we then fire it on zeta load bos which is one per zeta too.
	boolean clientTicked = false;
	public void clientTick(TickEvent.ClientTickEvent e) {
		if(!clientTicked) {
			ZetaList.INSTANCE.fireLoadEvent(new ZFirstClientTick());
			clientTicked = true;
		}
	}

	public void renderGuiOverlayPre(RenderGuiOverlayEvent.Pre e) {
		NamedGuiOverlay overlay = e.getOverlay();
		if (overlay == VanillaGuiOverlay.HOTBAR.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Pre(e), ZRenderGuiOverlay.Hotbar.Pre.class);
		else if (overlay == VanillaGuiOverlay.CROSSHAIR.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Pre(e), ZRenderGuiOverlay.Crosshair.Pre.class);
		else if (overlay == VanillaGuiOverlay.PLAYER_HEALTH.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Pre(e), ZRenderGuiOverlay.PlayerHealth.Pre.class);
		else if (overlay == VanillaGuiOverlay.ARMOR_LEVEL.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Pre(e), ZRenderGuiOverlay.ArmorLevel.Pre.class);
		else if (overlay == VanillaGuiOverlay.DEBUG_TEXT.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Pre(e), ZRenderGuiOverlay.DebugText.Pre.class);
		else if (overlay == VanillaGuiOverlay.POTION_ICONS.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Pre(e), ZRenderGuiOverlay.PotionIcons.Pre.class);
		else if (overlay == VanillaGuiOverlay.CHAT_PANEL.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Pre(e), ZRenderGuiOverlay.ChatPanel.Pre.class);
	}

	public void renderGuiOverlayPost(RenderGuiOverlayEvent.Post e) {
		NamedGuiOverlay overlay = e.getOverlay();
		if (overlay == VanillaGuiOverlay.HOTBAR.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Post(e), ZRenderGuiOverlay.Hotbar.Post.class);
		else if (overlay == VanillaGuiOverlay.CROSSHAIR.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Post(e), ZRenderGuiOverlay.Crosshair.Post.class);
		else if (overlay == VanillaGuiOverlay.PLAYER_HEALTH.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Post(e), ZRenderGuiOverlay.PlayerHealth.Post.class);
		else if (overlay == VanillaGuiOverlay.ARMOR_LEVEL.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Post(e), ZRenderGuiOverlay.ArmorLevel.Post.class);
		else if (overlay == VanillaGuiOverlay.DEBUG_TEXT.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Post(e), ZRenderGuiOverlay.DebugText.Post.class);
		else if (overlay == VanillaGuiOverlay.POTION_ICONS.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Post(e), ZRenderGuiOverlay.PotionIcons.Post.class);
		else if (overlay == VanillaGuiOverlay.CHAT_PANEL.type())
			clientZeta.playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Post(e), ZRenderGuiOverlay.ChatPanel.Post.class);
	}
	
}
