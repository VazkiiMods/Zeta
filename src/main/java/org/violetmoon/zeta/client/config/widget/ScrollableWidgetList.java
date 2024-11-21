package org.violetmoon.zeta.client.config.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;

public class ScrollableWidgetList<S extends Screen, E extends ScrollableWidgetList.Entry<E>> extends ObjectSelectionList<E> {
	public final S parent;

	public ScrollableWidgetList(S parent) {
		super(Minecraft.getInstance(), parent.width, parent.height - 80, 40, 30);
		this.parent = parent;
	}

	//protected -> public
	@Override
	public int addEntry(E entry) {
		return super.addEntry(entry);
	}

	//protected -> public
	@Override
	public void replaceEntries(Collection<E> newEntries) {
		super.replaceEntries(newEntries);
	}

	//private -> public
	public void scroll2(int amt) {
		this.setScrollAmount(this.getScrollAmount() + (double)amt);
	}

	//protected -> public, and made more convenient (takes index instead of Entry)
	public void ensureVisible2(int index) {
		int i = this.getRowTop(index);
		int j = i - this.getY() - 4 - this.itemHeight;
		if (j < 0)
			this.scroll2(j);

		int k = this.getBottom() - i - this.itemHeight - this.itemHeight;
		if (k < 0)
			this.scroll2(-k);
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	public void forEachWidgetWrapper(Consumer<WidgetWrapper> action) {
		children().forEach(e -> e.children.forEach(action));
	}

	//Taking Consumers b/c these methods on Screen are protected. Just pass this::addRenderableWidget
	public void addChildWidgets(Consumer<AbstractWidget> addRenderableWidget, Consumer<AbstractWidget> addWidget) {
		forEachWidgetWrapper(w -> {
			if(w.widget instanceof Button)
				addRenderableWidget.accept(w.widget);
			else
				addWidget.accept(w.widget);
		});
	}

	public void removeChildWidgets(Consumer<AbstractWidget> removeWidget) {
		forEachWidgetWrapper(w -> removeWidget.accept(w.widget));
	}

	// Intended flow (from Screen.render):
	//
	// list.render(mstack, x, y, pt);
	// super.render(mstack, x, y, pt);
	// list.reenableVisibleWidgets();

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		//turn off wasOnScreen, then render widget - minecraft does some simple culling when rendering,
		//and as a side effect of Entry.render, wasOnScreen will be turned back on
		forEachWidgetWrapper(w -> {
			w.widget.visible = false;
			w.wasOnScreen = false;
		});
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
	}

	public void reenableVisibleWidgets() {
		//if a widget was on screen, re-enable its "visible" flag so you can click on them
		forEachWidgetWrapper(w -> {
			if(w.wasOnScreen)
				w.widget.visible = true;
		});
	}

	public static abstract class Entry<E extends ScrollableWidgetList.Entry<E>> extends ObjectSelectionList.Entry<E> {
		public List<WidgetWrapper> children = new ArrayList<>();

		public void addScrollingWidget(AbstractWidget e) {
			children.add(new WidgetWrapper(e));
		}

		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			children.forEach(c -> {
				c.updatePosition(rowLeft, rowTop);

				c.wasOnScreen = true;

				//only enable the visible flag as long as needed to render the widget
				c.widget.visible = true;
				c.widget.render(guiGraphics, mouseX, mouseY, partialTicks);
				c.widget.visible = false;
			});
		}

		//Convenience for drawing a striped background
		public void drawBackground(GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			if(index % 2 == 0)
				guiGraphics.fill(rowLeft, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0x66000000);

			if(hovered) {
				guiGraphics.fill(rowLeft, rowTop, rowLeft + 1, rowTop + rowHeight, 0xFFFFFFFF);
				guiGraphics.fill(rowLeft + rowWidth - 1, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);

				guiGraphics.fill(rowLeft, rowTop, rowLeft + rowWidth, rowTop + 1, 0xFFFFFFFF);
				guiGraphics.fill(rowLeft, rowTop + rowHeight - 1, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);
			}
		}
	}

	public static class WidgetWrapper {
		public final AbstractWidget widget;
		public final int relativeX, relativeY;

		public boolean wasOnScreen = false;

		public WidgetWrapper(AbstractWidget widget) {
			this.widget = widget;
			this.relativeX = widget.getX();
			this.relativeY = widget.getY();
		}

		public void updatePosition(int currX, int currY) {
			widget.setX(currX + relativeX);
			widget.setY(currY + relativeY);
			widget.visible = true;
		}

	}
}
