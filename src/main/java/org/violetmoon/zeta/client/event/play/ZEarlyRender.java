package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zetaimplforge.mixin.mixins.client.GameRenderMixin;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Called inside {@link GameRenderMixin}
 * <br>
 * Injected into {@link net.minecraft.client.renderer.GameRenderer} after
 * <pre>{@code this.minecraft.getProfiler().popPush("gui");}</pre>
 */
public interface ZEarlyRender extends IZetaPlayEvent {
    GuiGraphics guiGraphics();
}

