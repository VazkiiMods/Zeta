package org.violetmoon.zetaimplforge.client.event.play;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.client.event.play.ZEarlyRender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ForgeZEarlyRender extends Event implements ZEarlyRender {
    @Override
    public GuiGraphics guiGraphics() {
        return new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
    }
}
