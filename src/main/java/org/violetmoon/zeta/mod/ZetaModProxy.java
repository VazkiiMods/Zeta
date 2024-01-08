package org.violetmoon.zeta.mod;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.util.handler.FuelHandler;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;

public class ZetaModProxy {

	public void registerEvents(Zeta zeta) {
		zeta.loadBus
			.subscribe(RecipeCrawlHandler.class)
			.subscribe(ToolInteractionHandler.class)
			.subscribe(EntitySpawnHandler.class)
			.subscribe(WorldGenHandler.class);
		
		zeta.playBus
			.subscribe(RecipeCrawlHandler.class)
			.subscribe(ToolInteractionHandler.class)
			.subscribe(SyncedFlagHandler.class);
	}
	
	// Cast up obj to ZetaClient on the client proxy
	public void setClientZeta(Object obj) { 
		// NO-OP
	}
	
}
