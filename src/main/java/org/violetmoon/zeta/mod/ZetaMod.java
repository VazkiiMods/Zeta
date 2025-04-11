package org.violetmoon.zeta.mod;

import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.violetmoon.zeta.Zeta;

public class ZetaMod {

	public static final String ZETA_ID = "zeta";
	public static final Logger LOGGER = LogManager.getLogger(ZETA_ID);

	//zeta mod own zeta thing
	public static Zeta ZETA;

    public ZetaMod(Zeta zeta) {
        ZETA = zeta;
        start();
    }
	
	public static void start(Zeta zeta, ZetaModProxy proxy, IEventBus bus) {
        ZETA.start();
        ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);

        ZetaModInternalNetwork.init();
	}
}