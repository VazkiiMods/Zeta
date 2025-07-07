package org.violetmoon.zeta.client.config.screen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public abstract class AbstractEditBoxInputScreen<T> extends AbstractInputScreen<T> {
	protected EditBox input;

	protected int VALID_COLOR = 0xE0E0E0; //EditBox.textColor
	protected int INVALID_COLOR = 0xDD3322;

	public AbstractEditBoxInputScreen(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<T> valueDef) {
		super(zc, parent, changes, valueDef);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		guiGraphics.drawCenteredString(font, Component.literal(def.getTranslatedDisplayName(I18n::get)).withStyle(ChatFormatting.BOLD), width / 2, 20, 0xFFFFFF);
		guiGraphics.drawCenteredString(font, I18n.get("zeta.gui.config.defaultvalue", def.defaultValue), width / 2, 30, 0xFFFFFF);
		input.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void init() {
		super.init();

		input = new EditBox(font, width / 2 - 100, 60, 200, 20, Component.literal(""));
		//input.setFilter(s -> fromString(s) != null); //ALlow temporarily editing incorrect values
		input.setMaxLength(maxStringLength());
		input.setResponder(this::onEdit);

		forceUpdateWidgetsTo(get());

		setInitialFocus(input);
		addWidget(input);
	}

	protected void onEdit(String newString) {
		T parsed = fromString(newString);
		if(parsed != null && def.validate(parsed) && newString.length() < maxStringLength()) {
			set(parsed);
			input.setTextColor(VALID_COLOR);
			updateButtonStatus(true);
		} else {
			input.setTextColor(INVALID_COLOR);
			updateButtonStatus(false);
		}
	}

	@Override
	protected void forceUpdateWidgetsTo(T value) {
		//Test that the object isnt in some state where it'll be rejected
		String asString = toString(value);
		T roundtrip = fromString(asString);
		if(roundtrip == null)
			input.setValue(toString(def.defaultValue));
		else
			input.setValue(asString);

		setInitialFocus(input);
	}

	protected String toString(T thing) {
		return thing.toString();
	}

	protected int maxStringLength() {
		return 256;
	}

	protected abstract @Nullable T fromString(String string);
}
