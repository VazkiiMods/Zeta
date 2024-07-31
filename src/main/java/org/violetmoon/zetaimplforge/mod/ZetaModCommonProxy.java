package org.violetmoon.zetaimplforge.mod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

public class ZetaModCommonProxy {

	public ZetaModCommonProxy(Zeta zeta) {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);

		zeta.loadBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(EntitySpawnHandler.class)
				.subscribe(WorldGenHandler.class)
				.subscribe(ZetaGeneralConfig.class);

		zeta.playBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(SyncedFlagHandler.class);


		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
		ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(ConfigEventDispatcher::dispatchAllInitialLoads);
	}
	
}
