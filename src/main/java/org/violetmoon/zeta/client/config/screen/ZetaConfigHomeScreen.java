package org.violetmoon.zeta.client.config.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.widget.CategoryButton;
import org.violetmoon.zeta.client.config.widget.CheckboxButton;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;
import org.violetmoon.zeta.module.ZetaCategory;

import java.util.ArrayList;
import java.util.List;

public class ZetaConfigHomeScreen extends ZetaScreen {
	public ZetaConfigHomeScreen(ZetaClient zc, Screen parent) {
		super(zc, parent);
		changeSet = new ChangeSet(z.configInternals);
	}

	protected final ChangeSet changeSet;
	protected Button saveButton;

	@Override
	protected void init() {
		super.init();

		List<ZetaCategory> categories = z.modules.getInhabitedCategories();
		@Nullable SectionDefinition generalSection = z.configManager.getGeneralSection();

		int buttonCount = categories.size();
		if(generalSection != null)
			buttonCount++;

		final int perLine = 3;
		List<Integer> categoryButtonXPositions = new ArrayList<>(buttonCount);
		for(int i = 0; i < buttonCount; i += perLine)
			categoryButtonXPositions.addAll(centeredRow(width / 2, 120, 10, Math.min(buttonCount - i, perLine)));

		for(int i = 0; i < buttonCount; i++) {
			int row = i / perLine;

			int x = categoryButtonXPositions.get(i);
			int y = 70 + row * 23;
			int bWidth = 120;

			if(i < categories.size()) {
				//a category button
				ZetaCategory category = categories.get(i);
				ValueDefinition<Boolean> categoryEnabled = z.configManager.getCategoryEnabledOption(category);
				SectionDefinition categorySection = z.configManager.getCategorySection(category);

				bWidth -= 20; //room for the checkbox
				Button mainButton = addRenderableWidget(new CategoryButton(x, y, bWidth, 20, componentFor(categorySection), category.icon.get(),
					b -> Minecraft.getInstance().setScreen(new SectionScreen(zc, this, changeSet, categorySection))));
				Button checkButton = addRenderableWidget(new CheckboxButton(zc, x + bWidth, y, changeSet, categoryEnabled));

				boolean active = category.requiredModsLoaded(z);
				mainButton.active = active;
				checkButton.active = active;
			} else {
				assert generalSection != null;
				addRenderableWidget(new Button.Builder(componentFor(generalSection),
					b -> Minecraft.getInstance().setScreen(new SectionScreen(zc, this, changeSet, generalSection)))
						.size(bWidth, 20).pos(x, y).build());
			}
		}

		//save
		saveButton = addRenderableWidget(new Button.Builder(componentForSaveButton(), this::commit).size(200, 20).pos(width / 2 - 100, height - 30).build());
	}

	public List<Integer> centeredRow(int centerX, int buttonWidth, int hpad, int count) {
		// https://i.imgur.com/ozRA3xw.png

		int slop = (count % 2 == 0 ? hpad : buttonWidth) / 2;
		int fullButtonsLeftOfCenter = count / 2; //rounds down
		int fullPaddingsLeftOfCenter = Math.max(0, (count - 1) / 2); //rounds down
		int startX = centerX - slop - (fullButtonsLeftOfCenter * buttonWidth) - (fullPaddingsLeftOfCenter * hpad);

		List<Integer> result = new ArrayList<>(count);
		int x = startX;
		for(int i = 0; i < count; i++) {
			result.add(x);
			x += buttonWidth + hpad;
		}
		return result;
	}

	private Component componentFor(SectionDefinition section) {
		MutableComponent comp = Component.translatable(z.modid + ".category." + section.name);

		if(changeSet.isDirty(section))
			comp.append(Component.literal("*").withStyle(ChatFormatting.GOLD));

		return comp;
	}

	private Component componentForSaveButton() {
		MutableComponent comp = Component.translatable("quark.gui.config.save");
		int changeCount = changeSet.changeCount();
		if(changeCount > 0)
			comp.append(" (")
				.append(Component.literal(String.valueOf(changeCount)).withStyle(ChatFormatting.GOLD))
				.append(")");

		return comp;
	}

	public void commit(Button button) {
		changeSet.applyAllChanges();
		returnToParent();
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(font, ChatFormatting.BOLD + I18n.get("quark.gui.config.header", WordUtils.capitalizeFully(z.modid)), width / 2, 15, 0x48ddbc);
	}

	@Override
	public void tick() {
		super.tick();

		//done here (instead of in init()), mainly so pressing a category checkbox will update the state of the button
		saveButton.setMessage(componentForSaveButton());
	}
}
