package org.violetmoon.zeta.util;

import org.violetmoon.zeta.multiloader.Env;

import java.util.function.Supplier;

/**
 * Don't confuse with logical side.
 * These two are better called as "Local Game" and "Dedicated Server"
 */
public enum ZetaSide {
	CLIENT, SERVER;

	public static ZetaSide fromClient(boolean isClient) {
		return isClient ? CLIENT : SERVER;
	}

	public boolean isDedicatedServer(){
		return this == SERVER;
	}

	public boolean isLocalGame(){
		return this == CLIENT;
	}

}
