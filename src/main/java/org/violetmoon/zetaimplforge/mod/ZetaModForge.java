package org.violetmoon.zetaimplforge.mod;

import org.apache.logging.log4j.LogManager;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zetaimplforge.ForgeZeta;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("zeta")
public class ZetaModForge {
	
	public ZetaModForge() {
		ForgeZeta zeta = new ForgeZeta(Zeta.ZETA_ID, LogManager.getLogger(Zeta.ZETA_ID + "-internal"));

		// creates 2 dist specific objects that will handle zeta specific & loader specific events needed for zeta to work
		DistExecutor.runForDist(() -> () -> new ZetaModClientProxy(zeta), () -> () -> new ZetaModCommonProxy(zeta));

		ZetaMod.start(zeta);
	}

	
}
