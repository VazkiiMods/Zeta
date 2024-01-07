package org.violetmoon.zeta;

import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;

public class ZetaMod {

	public static Zeta ZETA;

	public static void start(Zeta zeta) {
		ZETA = zeta;

		ZETA.start();
		ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);
		
		ZetaModInternalNetwork.init();
	}

}
