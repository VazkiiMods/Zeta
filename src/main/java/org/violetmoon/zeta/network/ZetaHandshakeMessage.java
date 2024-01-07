package org.violetmoon.zeta.network;

import java.util.function.IntSupplier;

public abstract class ZetaHandshakeMessage implements IntSupplier, IZetaMessage {

	private transient int loginIndex;

	public void setLoginIndex(final int loginIndex) {
		this.loginIndex = loginIndex;
	}

	public int getLoginIndex() {
		return loginIndex;
	}

	@Override
	public int getAsInt() {
		return loginIndex;
	}

}
