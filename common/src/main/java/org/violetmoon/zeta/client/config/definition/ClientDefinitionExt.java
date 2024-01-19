package org.violetmoon.zeta.client.config.definition;

import java.util.function.Consumer;

import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.Definition;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public interface ClientDefinitionExt<T extends Definition> {
	String getSubtitle(ChangeSet changes, T def);

	void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, T def, Consumer<AbstractWidget> widgets);

	default String truncate(String in) {
		if(in.length() > 30)
			return in.substring(0, 27) + "...";
		else
			return in;
	}

	class Default implements ClientDefinitionExt<Definition> {
		@Override
		public String getSubtitle(ChangeSet changes, Definition def) {
			return "";
		}

		@Override
		public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, Definition def, Consumer<AbstractWidget> widgets) {

		}
	}
}
