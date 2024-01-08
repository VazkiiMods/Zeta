package org.violetmoon.zeta;

import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;
import org.violetmoon.zeta.util.handler.FuelHandler;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;

public class ZetaMod {

	public static Zeta ZETA;

	public static void start(Zeta zeta) {
		ZETA = zeta;

		ZETA.start();
		ZETA.loadModules(null, null, ZetaGeneralConfig.INSTANCE);
		
		ZetaModInternalNetwork.init();
		
		ZETA.loadBus.subscribe(FuelHandler.class)
		.subscribe(RecipeCrawlHandler.class)
		.subscribe(ToolInteractionHandler.class)
		.subscribe(EntitySpawnHandler.class)
		.subscribe(WorldGenHandler.class);
		
		ZETA.playBus.subscribe(FuelHandler.class)
		.subscribe(RecipeCrawlHandler.class)
		.subscribe(ToolInteractionHandler.class);

	}

}
