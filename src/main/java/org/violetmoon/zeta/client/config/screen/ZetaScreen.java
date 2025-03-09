package org.violetmoon.zeta.client.config.screen;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ZetaClient;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ZetaScreen extends Screen {

	private final Screen parent;

	protected final Zeta z;
	protected final ZetaClient zc;

	public ZetaScreen(ZetaClient zc, Component title, Screen parent) {
		super(title);
		this.parent = parent;

		this.z = zc.zeta;
		this.zc = zc;
	}

	public ZetaScreen(ZetaClient zc, Screen parent) {
		this(zc, Component.empty(), parent);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Deprecated(forRemoval = true) //unnecessary distinction, vanilla screens return to parent on onClose as well
	public void returnToParent() {
		onClose();
	}
}
