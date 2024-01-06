package org.violetmoon.zetaimplforge.client.event.play;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.violetmoon.zeta.client.event.play.ZEarlyRender;

public class ForgeZEarlyRender implements ZEarlyRender {
    @Override
    public GuiGraphics guiGraphics() {
        return new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
    }
}
