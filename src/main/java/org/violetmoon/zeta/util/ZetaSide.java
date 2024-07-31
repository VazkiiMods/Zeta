package org.violetmoon.zeta.util;

/**
 * Don't confuse with logical side.
 * These two are better called as "Local Game" and "Dedicated Server"
 */
public enum ZetaSide {
	CLIENT, SERVER;

	public static ZetaSide fromClient(boolean isClient) {
		return isClient ? CLIENT : SERVER;
	}
}
