package org.violetmoon.zeta.client.event.play;

import net.minecraft.client.gui.GuiGraphics;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zetaimplforge.mixin.client.GameRenderMixin;

/**
 * Called inside {@link GameRenderMixin}
 * <br>
 * Injected into {@link net.minecraft.client.renderer.GameRenderer} after
 * <pre>{@code this.minecraft.getProfiler().popPush("gui");}</pre>
 */
public interface ZEarlyRender extends IZetaPlayEvent {
    GuiGraphics guiGraphics();
}

