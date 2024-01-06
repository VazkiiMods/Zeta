package org.violetmoon.zeta.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.event.bus.PlayEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Deprecated // Very hacky for what it does.
public class TopLayerTooltipHandler {

	private List<Component> tooltip;
	private int tooltipX, tooltipY;

	@PlayEvent
	public void renderTick(ZRenderTick event) {
		if(tooltip != null && event.isEndPhase()) {
			Minecraft mc = Minecraft.getInstance();
			Screen screen = mc.screen;

			Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
			VertexSorting vertexSorting = RenderSystem.getVertexSorting();

			// Set correct projection matrix
			Window window = mc.getWindow();
			Matrix4f matrix4f = new Matrix4f().setOrtho(
					0.0F,
					(float) ((double) window.getWidth() / window.getGuiScale()),
					(float) ((double) window.getHeight() / window.getGuiScale()),
					0.0F,
					1000.0F,
					net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane()
			);
			RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
			PoseStack posestack = RenderSystem.getModelViewStack();
			posestack.pushPose();
			posestack.setIdentity();
			posestack.translate(0.0D, 0.0D, 1000F - net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());
			RenderSystem.applyModelViewMatrix();
			// End

			GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());

			if(screen != null)
				guiGraphics.renderTooltip(mc.font, tooltip, Optional.empty(), tooltipX, tooltipY);

			// Reset projection matrix
			guiGraphics.flush();
			posestack.popPose();
			RenderSystem.applyModelViewMatrix();
			RenderSystem.setProjectionMatrix(projectionMatrix, vertexSorting);
			// End

			tooltip = null;
		}
	}

	public void setTooltip(List<String> tooltip, int x, int y) {
		this.tooltip = tooltip.stream().map(Component::literal).collect(Collectors.toList());
		this.tooltipX = x;
		this.tooltipY = y;
	}

}
