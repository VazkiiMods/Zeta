package org.violetmoon.zetaimplforge.client.event.play;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.event.bus.ZPhase;

public class ForgeZClientTick implements ZClientTick {
    private final ClientTickEvent e;

    public ForgeZClientTick(ClientTickEvent e) {
        this.e = e;
    }

    @Override
    public ZPhase getPhase() {
        return from(e instanceof ClientTickEvent.Pre);
    }

    public static ZPhase from(boolean bool) {
        return (bool) ? ZPhase.START : ZPhase.END;
    }
}

