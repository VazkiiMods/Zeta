package org.violetmoon.zeta.client;

import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.bus.ZPhase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.violetmoon.zeta.event.play.ZRecipeCrawl;

//TODO: 1.21. replace with minecraft own ticker. Tbh this is legacy already and should be replaced with Minecraft.getPartialTicks()
@Deprecated
public final class ClientTicker {

	//no need to have more than 1 instance of this class. Ticks are always the same
	public static final ClientTicker INSTANCE = new ClientTicker();

	private ClientTicker() {
	}

	public int ticksInGame = 0;
	public float partialTicks = 0;
	public float delta = 0;
	public float total = 0;

	@ApiStatus.Internal
	@PlayEvent
	public void onRenderTick(ZRenderTick event) {
		if(event.isStartPhase())
			partialTicks = event.getRenderTickTime();
		else
			endRenderTick();
	}

	@ApiStatus.Internal
	@PlayEvent
	public void onEndClientTick(ZClientTick.Start event) {
		if(event.getPhase() != ZPhase.END)
			return;

		Screen gui = Minecraft.getInstance().screen;
		if(gui == null || !gui.isPauseScreen()) {
			ticksInGame++;
			partialTicks = 0;
		}

		endRenderTick();
	}

	private void endRenderTick() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}
}
