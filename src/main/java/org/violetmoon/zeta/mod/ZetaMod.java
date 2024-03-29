package org.violetmoon.zeta.mod;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;

public class ZetaMod {

	public static Zeta ZETA;
	public static ZetaModProxy proxy;
	
	public static void start(Zeta zeta, ZetaModProxy proxy) {
		ZetaMod.ZETA = zeta;
		ZetaMod.proxy = proxy;

		ZETA.start();
		ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);
		
		ZetaModInternalNetwork.init();
		proxy.registerEvents(zeta);
	}

}
