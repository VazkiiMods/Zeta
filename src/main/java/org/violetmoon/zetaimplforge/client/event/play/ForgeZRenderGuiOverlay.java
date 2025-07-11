package org.violetmoon.zetaimplforge.client.event.play;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ForgeZRenderGuiOverlay implements ZRenderGuiOverlay {
	public final RenderGuiLayerEvent e;

	public ForgeZRenderGuiOverlay(RenderGuiLayerEvent e) {
		this.e = e;
	}

	@Override
	public Window getWindow() {
		return Minecraft.getInstance().getWindow();
	}

	@Override
	public GuiGraphics getGuiGraphics() {
		return e.getGuiGraphics();
	}

	@Override
	public DeltaTracker getPartialTick() {
		return e.getPartialTick();
	}

	@Override
	public boolean shouldDrawSurvivalElements() {
		return Minecraft.getInstance().gameMode.canHurtPlayer();
	}

	@Override
	public int getLeftHeight() {
		return Minecraft.getInstance().gui.leftHeight;
	}

    public ResourceLocation getLayerName() {
        return e.getName();
    }

    public LayeredDraw.Layer getLayer() {
        return e.getLayer();
    }

    public static class Pre extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Pre {
		public Pre(RenderGuiLayerEvent.Pre e) {
			super(e);
		}
	}

	public static class Post extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Post {
		public Post(RenderGuiLayerEvent.Post e) {
			super(e);
		}
	}
}
