package org.violetmoon.zetaimplforge.mod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientTicker;
import org.violetmoon.zeta.util.handler.RequiredModTooltipHandler;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zetaimplforge.event.load.ForgeZFirstClientTick;

public class ZetaModClientProxy extends ZetaModCommonProxy {

    public ZetaModClientProxy(Zeta zeta) {
        super(zeta);

        zeta.playBus
                .subscribe(ClientTicker.INSTANCE)
                .subscribe(new RequiredModTooltipHandler.Client(zeta));

        MinecraftForge.EVENT_BUS.addListener(this::clientTick);
    }

    // added once per zeta. Its fine as we then fire it on zeta load bos which is one per zeta too.
    boolean clientTicked = false;

    public void clientTick(TickEvent.ClientTickEvent e) {
        if (!clientTicked) {
            ZetaList.INSTANCE.fireLoadEvent(new ForgeZFirstClientTick());
            clientTicked = true;
        }
    }

}
