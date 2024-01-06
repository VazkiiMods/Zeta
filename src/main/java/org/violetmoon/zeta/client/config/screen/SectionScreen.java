package org.violetmoon.zeta.client.config.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.definition.ClientDefinitionExt;
import org.violetmoon.zeta.client.config.widget.DefaultDiscardDone;
import org.violetmoon.zeta.client.config.widget.ScrollableWidgetList;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.Definition;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SectionScreen extends ZetaScreen {
	protected final SectionDefinition section;
	protected final ChangeSet changes;
	protected final String breadcrumbs;

	protected DefaultDiscardDone defaultDiscardDone;
	protected ScrollableWidgetList<SectionScreen, Entry> list;

	public SectionScreen(ZetaClient zc, Screen parent, ChangeSet changes, SectionDefinition section) {
		super(zc, parent);
		this.section = section;
		this.changes = changes;
		this.breadcrumbs = "> " + String.join(" > ", section.path);
	}

	@Override
	protected void init() {
		super.init();
		double previousScrollAmount = list == null ? 0 : list.getScrollAmount(); //for preserving scroll across re-init

		//first append the default/discard/done buttons, then add the scrolling list. this way the buttons will take click priority
		this.defaultDiscardDone = new DefaultDiscardDone(this, changes, section);
		defaultDiscardDone.addWidgets(this::addRenderableWidget);

		this.list = new ScrollableWidgetList<>(this);

		for(ValueDefinition<?> value : section.getValues())
			list.addEntry(new ValueDefinitionEntry<>(changes, value));

		Collection<SectionDefinition> subsections = section.getSubsections();
		if(!subsections.isEmpty()) {
			list.addEntry(new Divider());

			for(SectionDefinition subsection : section.getSubsections())
				list.addEntry(new SectionDefinitionEntry(changes, subsection));
		}

		addWidget(list);
		list.addChildWidgets(this::addRenderableWidget, this::addWidget);
		list.setScrollAmount(previousScrollAmount);

		defaultDiscardDone.discard.active = changes.isDirty(section);
	}

	@Override
	public void tick() {
		defaultDiscardDone.discard.active = changes.isDirty(section);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);

		list.render(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		list.reenableVisibleWidgets();

		int left = 20;

		String modName = WordUtils.capitalizeFully(z.modid);
		guiGraphics.drawString(font, ChatFormatting.BOLD + I18n.get("quark.gui.config.header", modName), left, 10, 0x48ddbc);
		guiGraphics.drawString(font, breadcrumbs, left, 20, 0xFFFFFF);
	}

	public abstract static class Entry extends ScrollableWidgetList.Entry<Entry> { }

	public class Divider extends Entry {
		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			assert minecraft != null;

			String s = I18n.get("quark.gui.config.subcategories");
			guiGraphics.drawString(minecraft.font, s, rowLeft + (float) (rowWidth / 2 - minecraft.font.width(s) / 2), rowTop + 7, 0x6666FF, true);
		}

		@Override
		public @NotNull Component getNarration() {
			return Component.literal("");
		}
	}

	public class DefinitionEntry<T extends Definition> extends Entry {
		private final ChangeSet changes;
		private final T def;
		private final ClientDefinitionExt<T> ext;

		public DefinitionEntry(ChangeSet changes, T def) {
			this.changes = changes;
			this.def = def;

			this.ext = zc.clientConfigManager.getExt(def);
			ext.addWidgets(zc, SectionScreen.this, changes, def, this::addScrollingWidget);
		}

		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			assert minecraft != null; //thank you intellij, always lookin out for me

			int left = rowLeft + 10;
			int top = rowTop + 4;

			int effIndex = index + 1;
			if(def instanceof SectionDefinition)
				effIndex--; // compensate for the divider
			drawBackground(guiGraphics, effIndex, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered);

			super.render(guiGraphics, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered, partialTicks);

			String name = def.getTranslatedDisplayName(I18n::get);
			if(changes.isDirty(def))
				name += ChatFormatting.GOLD + "*";

			int len = minecraft.font.width(name);
			int maxLen = rowWidth - 85;
			String originalName = null;
			if(len > maxLen) {
				originalName = name;
				do {
					name = name.substring(0, name.length() - 1);
					len = minecraft.font.width(name);
				} while(len > maxLen);

				name += "...";
			}

			List<Component> tooltip = def.getTranslatedComment(I18n::get)
				.stream()
				.map(Component::literal) //TODO: return a TranslatableComponent from this api instead?
				.collect(Collectors.toList());

			if(originalName != null) {
				if(tooltip.isEmpty()) {
					tooltip.add(Component.literal(originalName));
				} else {
					tooltip.add(0, Component.empty());
					tooltip.add(0, Component.literal(originalName));
				}
			}

			if(!tooltip.isEmpty()) {
				int hoverLeft = left + minecraft.font.width(name + " ");
				int hoverRight = hoverLeft + minecraft.font.width("(?)");

				name += (ChatFormatting.AQUA + " (?)");
				if(mouseX >= hoverLeft && mouseX < hoverRight && mouseY >= top && mouseY < (top + 10))
					guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
			}

			guiGraphics.drawString(minecraft.font, name, left, top, 0xFFFFFF, true);
			if(ext != null)
				guiGraphics.drawString(minecraft.font, ext.getSubtitle(changes, def), left, top + 10, 0x999999, true);
		}

		@Override
		public @NotNull Component getNarration() {
			return Component.literal(def.getTranslatedDisplayName(I18n::get));
		}
	}

	public class ValueDefinitionEntry<X> extends DefinitionEntry<ValueDefinition<X>> {
		public ValueDefinitionEntry(ChangeSet changes, ValueDefinition<X> def) {
			super(changes, def);
		}
	}

	public class SectionDefinitionEntry extends DefinitionEntry<SectionDefinition> {
		public SectionDefinitionEntry(ChangeSet changes, SectionDefinition def) {
			super(changes, def);
		}
	}
}
