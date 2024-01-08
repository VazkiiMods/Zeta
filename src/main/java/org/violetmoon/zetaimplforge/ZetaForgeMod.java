package org.violetmoon.zetaimplforge;

import org.apache.logging.log4j.LogManager;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.ZetaMod;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("zeta")
public class ZetaForgeMod {
	
	public ZetaForgeMod() {
		ZetaMod.start(new ForgeZeta(Zeta.ZETA_ID, LogManager.getLogger(Zeta.ZETA_ID + "-internal")));
		
		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
	}
	
}
