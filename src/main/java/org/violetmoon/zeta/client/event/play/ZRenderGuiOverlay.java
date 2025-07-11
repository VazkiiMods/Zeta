package org.violetmoon.zeta.client.event.play;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;

public interface ZRenderGuiOverlay extends IZetaPlayEvent {
	Window getWindow();
	GuiGraphics getGuiGraphics();
	DeltaTracker getPartialTick();
	ResourceLocation getLayerName();
	LayeredDraw.Layer getLayer();

	boolean shouldDrawSurvivalElements();
	int getLeftHeight(); //weird ForgeGui stuff

	interface Pre extends ZRenderGuiOverlay {}
	interface Post extends ZRenderGuiOverlay {}
}
