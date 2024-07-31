package org.violetmoon.zeta.mod;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;

public class ZetaMod {

	public static Zeta ZETA;

	public static void start(Zeta zeta) {
		ZetaMod.ZETA = zeta;

		ZETA.start();
		ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);
		
		ZetaModInternalNetwork.init();
	}

}
