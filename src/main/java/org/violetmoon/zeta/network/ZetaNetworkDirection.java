package org.violetmoon.zeta.network;

public enum ZetaNetworkDirection {
	PLAY_TO_SERVER,
	PLAY_TO_CLIENT,
	LOGIN_TO_SERVER,
	LOGIN_TO_CLIENT;

	public boolean isLogin() {
		return this == LOGIN_TO_SERVER || this == LOGIN_TO_CLIENT;
	}
}
