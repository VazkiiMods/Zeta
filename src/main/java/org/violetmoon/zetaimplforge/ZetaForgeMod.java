package org.violetmoon.zetaimplforge;

import org.apache.logging.log4j.LogManager;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.ZetaMod;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("zeta")
public class ZetaForgeMod {
	
	public ZetaForgeMod() {
		ZetaMod.start(new ForgeZeta(Zeta.ZETA_ID, LogManager.getLogger(Zeta.ZETA_ID + "-internal")));
		
		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
		ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
