package org.violetmoon.zeta.mod;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;

public class ZetaMod {

	public static final String ZETA_ID = "zeta";
	public static final Logger LOGGER = LogManager.getLogger(ZETA_ID);

	//zeta mod own zeta thing
	public static Zeta ZETA;

	public ZetaMod(Zeta zeta) {
		ZETA = zeta;

		ZetaModInternalNetwork.registerMessages(ZETA.network);
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(ZETA_ID, name);
	}
}
