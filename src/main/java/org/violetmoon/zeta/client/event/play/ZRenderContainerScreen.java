package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public interface ZRenderContainerScreen extends IZetaPlayEvent {
	AbstractContainerScreen<?> getContainerScreen();
	GuiGraphics getGuiGraphics();
	int getMouseX();
	int getMouseY();

	interface Foreground extends ZRenderContainerScreen { }
	interface Background extends ZRenderContainerScreen { }
}
