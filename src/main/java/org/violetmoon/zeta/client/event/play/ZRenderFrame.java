package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

/**
 * <p>
 * <b>Notice:</b> Using {@link net.minecraft.client.gui.GuiGraphics in this event
 * will not work, Use {@link ZEarlyRender } instead.
 * </p>
 */
public interface ZRenderFrame extends IZetaPlayEvent {
	float getRenderTickTime();

	interface Start extends ZRenderFrame {
	}

	interface End extends ZRenderFrame {
	}
}
