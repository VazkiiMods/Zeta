package org.violetmoon.zetaimplforge.client.event.play;

import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.violetmoon.zeta.client.event.play.ZRenderFrame;

public record ForgeZRenderFrame(RenderFrameEvent e) implements ZRenderFrame {
	@Override
	public float getRenderTickTime() {
		return e.getPartialTick().getRealtimeDeltaTicks(); //todo: Test this
	}

	@Override
	public boolean isEndPhase() {
		return e instanceof RenderFrameEvent.Post;
	}
}
