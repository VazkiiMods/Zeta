package org.violetmoon.zeta.client.config.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.widget.ScrollableWidgetList;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class StringListInputScreen extends AbstractInputScreen<List<String>> {
	protected ScrollableWidgetList<StringListInputScreen, Entry> list;

	public StringListInputScreen(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<List<String>> def) {
		super(zc, parent, changes, def);
	}

	@Override
	protected void init() {
		super.init();

		list = new ScrollableWidgetList<>(this);

		forceUpdateWidgetsTo(get());
	}

	@Override
	protected void forceUpdateWidgetsTo(List<String> value) {
		//out with the old
		removeWidget(list);
		list.removeChildWidgets(this::removeWidget);

		//in with the new
		list.replaceEntries( IntStream.range(0, value.size() + 1).mapToObj(Entry::new).toList());

		//re-clamp the scrollbar so when you remove an element, you aren't scrolled past the end
		//setScrollAmount has a clamp() call in it
		list.setScrollAmount(list.getScrollAmount());

		list.addChildWidgets(this::addRenderableWidget, this::addWidget);
		updateButtonStatus(def.validate(value));
		addWidget(list);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		//renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        //super.render(guiGraphics, mouseX, mouseY, partialTicks);
        list.render(guiGraphics, mouseX, mouseY, partialTicks);

        for(Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        list.reenableVisibleWidgets();

		guiGraphics.drawCenteredString(font, Component.literal(def.getTranslatedDisplayName(I18n::get)).withStyle(ChatFormatting.BOLD), width / 2, 20, 0xFFFFFF);
	}

	protected String getString(int index) {
		List<String> list = get();
		if(index < list.size())
			return list.get(index);
		else
			return null;
	}

	protected void setString(int index, String s) {
		List<String> copy = new ArrayList<>(get());
		copy.set(index, s);

		set(copy);
		//forceUpdateWidgetsTo(copy); //No strings added or removed -> no need to recreate the widget
		updateButtonStatus(def.validate(copy));
	}

	protected void add() {
		List<String> copy = new ArrayList<>(get());
		copy.add("");

		set(copy);
		forceUpdateWidgetsTo(copy);
		updateButtonStatus(def.validate(copy));

		list.ensureVisible2(copy.size() + 1); //scroll to the end of the list
	}

	protected void remove(int idx) {
		List<String> copy = new ArrayList<>(get());
		copy.remove(idx);

		set(copy);
		forceUpdateWidgetsTo(copy);
		updateButtonStatus(def.validate(copy));
	}

	protected class Entry extends ScrollableWidgetList.Entry<Entry> {
		private final int index;

		public Entry(int index) {
			this.index = index;

			String here = getString(index);
			if(getString(index) != null) {
				Minecraft mc = Minecraft.getInstance();
				EditBox field = new EditBox(mc.font, 10, 3, 210, 20, Component.literal(""));
				field.setMaxLength(256);
				field.setValue(here);
				field.moveCursorTo(0, false);
				field.setResponder(str -> setString(index, str));
				addScrollingWidget(field);

				addScrollingWidget(new Button.Builder(Component.literal("-").withStyle(ChatFormatting.RED), b -> remove(index)).size(20, 20).pos(230, 3).build());
			} else {
				addScrollingWidget(new Button.Builder(Component.literal("+").withStyle(ChatFormatting.GREEN), b -> add()).size(20, 20).pos(10, 3).build());
			}
		}

		@Override
		public @NotNull Component getNarration() {
			return Component.literal(Optional.ofNullable(getString(index)).orElse(""));
		}
	}
}
