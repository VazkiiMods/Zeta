package org.violetmoon.zetaimplforge.mod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.violetmoon.zeta.client.config.widget.ZButtonHandler;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zetaimplforge.EventTest;
import org.violetmoon.zetaimplforge.ForgeZeta;

@Mod(ZetaMod.ZETA_ID)
public class ZetaModForge extends ZetaMod {

	// this needs to be created before the mod constructor is as we need it to create the zeta bus.
	public static final ZetaModCommonProxy PROXY = (FMLEnvironment.dist == Dist.CLIENT) ? new ZetaModClientProxy() : new ZetaModCommonProxy();

	public ZetaModForge(IEventBus bus) {
		super(new ForgeZeta(ZetaMod.ZETA_ID, ZetaMod.LOGGER));

		// creates 2 dist specific objects that will handle zeta specific & loader specific events needed for zeta to work
		PROXY.registerEvents(ZETA);

		if(!ZETA.isProduction) {
			ZETA.loadBus.subscribe(EventTest.class);
			ZETA.playBus.subscribe(EventTest.class);
			if (FMLEnvironment.dist == Dist.CLIENT && !FMLEnvironment.production) ZETA.playBus.subscribe(ZButtonHandler.class);
		}
	}
}
