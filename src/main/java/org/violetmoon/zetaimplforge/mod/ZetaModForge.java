package org.violetmoon.zetaimplforge.mod;

import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zetaimplforge.ForgeZeta;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ZetaMod.ZETA_ID)
public class ZetaModForge extends ZetaMod{
	
	public ZetaModForge() {
		super(new ForgeZeta(ZetaMod.ZETA_ID, ZetaMod.LOGGER));

		// creates 2 dist specific objects that will handle zeta specific & loader specific events needed for zeta to work
		DistExecutor.runForDist(() -> () -> new ZetaModClientProxy(ZetaMod.ZETA), () -> () -> new ZetaModCommonProxy(ZetaMod.ZETA));

	}

	
}
