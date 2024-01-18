package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

/**
 * <p>
 * <b>Notice:</b> Using {@link net.minecraft.client.gui.GuiGraphics in this event
 * will not work, Use {@link ZEarlyRender } instead.
 * </p>
 */
public interface ZRenderTick extends IZetaPlayEvent {
	float getRenderTickTime();
	boolean isEndPhase();

	default boolean isStartPhase() {
		return !isEndPhase();
	}
}
