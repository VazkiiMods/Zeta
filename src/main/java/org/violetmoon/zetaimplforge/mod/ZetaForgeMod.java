package org.violetmoon.zetaimplforge.mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.mod.ZetaClientProxy;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.mod.ZetaModProxy;
import org.violetmoon.zeta.multiloader.Env;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zetaimplforge.ForgeZeta;
import org.violetmoon.zetaimplforge.client.ForgeZetaClient;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

@Mod("zeta")
public class ZetaForgeMod {
	
	public ZetaForgeMod(IEventBus bus) {
		ForgeZeta zeta = new ForgeZeta(Zeta.ZETA_ID, LogManager.getLogger(Zeta.ZETA_ID + "-internal"));
		
		ZetaModProxy proxy = Env.unsafeRunForDist(() -> ZetaClientProxy::new, () -> ZetaModProxy::new);
		Object zetaClient = Env.unsafeRunForDist(() -> () -> new ForgeZetaClient(zeta), () -> () -> new Object());
		
		ZetaMod.start(zeta, proxy);
		ZetaMod.proxy.setClientZeta(zetaClient);
		
		NeoForge.EVENT_BUS.register(ToolInteractionHandler.class);
		ZetaBiomeModifier.registerBiomeModifier(bus);
		
		bus.addListener(this::setup);
	}
	
	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(ConfigEventDispatcher::dispatchAllInitialLoads);
	}
	
}
