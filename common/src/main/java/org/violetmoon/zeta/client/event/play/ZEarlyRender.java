package org.violetmoon.zeta.client.event.play;

import net.minecraft.client.Minecraft;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Called inside GameRenderMixin
 * <br>
 * Injected into {@link net.minecraft.client.renderer.GameRenderer} after
 * <pre>{@code this.minecraft.getProfiler().popPush("gui");}</pre>
 */
public class ZEarlyRender implements IZetaPlayEvent {
    public GuiGraphics guiGraphics() {
        return new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
    }
}

