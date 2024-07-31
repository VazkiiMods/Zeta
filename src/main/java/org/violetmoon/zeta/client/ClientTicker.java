package org.violetmoon.zeta.client;

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

	public int ticksInGame = 0;
	public float partialTicks = 0;
	public float delta = 0;
	public float total = 0;

	@PlayEvent
	public void onRenderTick(ZRenderTick event) {
		if(event.isStartPhase())
			partialTicks = event.getRenderTickTime();
		else
			endRenderTick();
	}

	@PlayEvent
	public void onEndClientTick(ZClientTick event) {
		if(event.getPhase() != ZPhase.END)
			return;

		Screen gui = Minecraft.getInstance().screen;
		if(gui == null || !gui.isPauseScreen()) {
			ticksInGame++;
			partialTicks = 0;
		}

		endRenderTick();
	}

	@PlayEvent
	public void a(ZRecipeCrawl.Digest e){
		int aa = 1;
	}

	public void endRenderTick() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}
}
