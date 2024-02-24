package org.violetmoon.zeta.client.event.play;

import net.minecraftforge.event.TickEvent;

public record ForgeZRenderTick(TickEvent.RenderTickEvent e) implements ZRenderTick {
	@Override
	public float getRenderTickTime() {
		return e.renderTickTime;
	}

	@Override
	public boolean isEndPhase() {
		return e.phase == TickEvent.Phase.END;
	}
}
