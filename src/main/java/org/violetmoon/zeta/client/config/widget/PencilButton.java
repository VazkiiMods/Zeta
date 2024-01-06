package org.violetmoon.zeta.client.config.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.client.ZetaClient;

public class PencilButton extends Button {

	//pencil: u32 v0 to u48 v16
	private final ResourceLocation iconsTexture;

	public PencilButton(ResourceLocation iconsTexture, int x, int y, OnPress pressable) {
		super(new Builder(Component.literal(""), pressable).size(20, 20).pos(x, y));
		this.iconsTexture = iconsTexture;
	}

	public PencilButton(ZetaClient zc, int x, int y, OnPress pressable) {
		this(zc.generalIcons, x, y, pressable);
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		int u = 32;
		int v = 0;

		guiGraphics.blit(iconsTexture, getX() + 2, getY() + 1, u, v, 16, 16);
	}

}
