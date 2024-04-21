package org.violetmoon.zeta.fabric.mod;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.violetmoon.zeta.FabricZeta;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.mod.ZetaClientProxy;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.mod.ZetaModProxy;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;

public class ZetaFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricZeta zeta = new FabricZeta(Zeta.ZETA_ID, LogManager.getLogger(Zeta.ZETA_ID + "-internal"));

        ZetaModProxy proxy = ZetaSide.unsafeRunForDist(() -> ZetaClientProxy::new, () -> ZetaModProxy::new);
        Object zetaClient = ZetaSide.unsafeRunForDist(() -> () -> new ForgeZetaClient(zeta), () -> Object::new);

        ZetaMod.start(zeta, proxy);
        ZetaMod.proxy.setClientZeta(zetaClient);

        MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
        ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(ConfigEventDispatcher::dispatchAllInitialLoads);
    }
}
