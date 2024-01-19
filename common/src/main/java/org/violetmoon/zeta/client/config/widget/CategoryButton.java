package org.violetmoon.zeta.client.config.widget;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryButton extends Button {
	private final ItemStack icon;
	private final Component text;

	public CategoryButton(int x, int y, int w, int h, Component text, ItemStack icon, OnPress onClick) {
		super(x, y, w, h, Component.literal(""), onClick, Button.DEFAULT_NARRATION);
		this.icon = icon;
		this.text = text;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		if(!active)
			setTooltip(Tooltip.create(Component.translatable("quark.gui.config.missingaddon")));

		Minecraft mc = Minecraft.getInstance();
		guiGraphics.renderFakeItem(icon, getX() + 5, getY() + 2);

		int iconPad = (16 + 5) / 2;
		guiGraphics.drawCenteredString(mc.font, text, getX() + width / 2 + iconPad, getY() + (height - 8) / 2, getFGColor());
	}

	// Forge stuff
	public static final int UNSET_FG_COLOR = -1;
	protected int packedFGColor = UNSET_FG_COLOR;
	public int getFGColor() {
		if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
		return this.active ? 16777215 : 10526880; // White : Light Grey
	}
	public void setFGColor(int color) {
		this.packedFGColor = color;
	}
	public void clearFGColor() {
		this.packedFGColor = UNSET_FG_COLOR;
	}
}
