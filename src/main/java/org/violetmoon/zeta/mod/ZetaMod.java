package org.violetmoon.zeta.mod;

import net.neoforged.bus.api.IEventBus;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;

public class ZetaMod {

	public static Zeta ZETA;
	public static ZetaModProxy proxy;
	
	public static void start(Zeta zeta, ZetaModProxy proxy, IEventBus bus) {
		ZetaMod.ZETA = zeta;
		ZetaMod.proxy = proxy;

		ZETA.start(bus);
		ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);

		proxy.registerEvents(zeta);
	}
}