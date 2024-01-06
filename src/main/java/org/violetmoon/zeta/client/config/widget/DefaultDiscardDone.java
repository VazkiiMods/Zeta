package org.violetmoon.zeta.client.config.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.violetmoon.zeta.client.config.screen.ZetaScreen;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.Definition;

import java.util.function.Consumer;

public class DefaultDiscardDone {
	public final Button resetToDefault;
	public final Button discard;
	public final Button done;

	private final ZetaScreen screen;
	private final ChangeSet changes;
	private final Definition def;

	public DefaultDiscardDone(ZetaScreen screen, ChangeSet changes, Definition def) {
		int pad = 3;
		int bWidth = 121;
		int left = (screen.width - (bWidth + pad) * 3) / 2;
		int vStart = screen.height - 30;

		this.resetToDefault = new Button.Builder(Component.translatable("quark.gui.config.default"), this::resetToDefault).size(bWidth, 20).pos(left, vStart).build();
		this.discard = new Button.Builder(Component.translatable("quark.gui.config.discard"), this::discard).size(bWidth, 20).pos(left + bWidth + pad, vStart).build();
		this.done = new Button.Builder(Component.translatable("gui.done"), this::done).size(bWidth, 20).pos(left + (bWidth + pad) * 2, vStart).build();

		this.screen = screen;
		this.changes = changes;
		this.def = def;
	}

	public void addWidgets(Consumer<AbstractWidget> addRenderableWidgets) {
		addRenderableWidgets.accept(resetToDefault);
		addRenderableWidgets.accept(discard);
		addRenderableWidgets.accept(done);
	}

	public void resetToDefault(Button b) {
		changes.resetToDefault(def);
	}

	public void discard(Button b) {
		changes.removeChange(def);
	}

	public void done(Button b) {
		screen.returnToParent();
	}
}
