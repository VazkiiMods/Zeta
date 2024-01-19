package org.violetmoon.zeta.util;

public enum ZetaSide {
	CLIENT, SERVER;

	public static ZetaSide fromClient(boolean isClient) {
		return isClient ? CLIENT : SERVER;
	}
}
