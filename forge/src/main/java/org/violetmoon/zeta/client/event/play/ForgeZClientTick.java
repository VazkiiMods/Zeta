package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.event.bus.ZPhase;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class ForgeZClientTick implements ZClientTick {
    private final ClientTickEvent e;

    public ForgeZClientTick(ClientTickEvent e) {
        this.e = e;
    }

    @Override
    public ZPhase getPhase() {
        return from(e.phase);
    }

    public static ZPhase from(Phase r) {
        return switch(r) {
            case START -> ZPhase.START;
            case END -> ZPhase.END;
        };
    }

    public static Phase to(ZPhase r) {
        return switch(r) {
            case START -> Phase.START;
            case END -> Phase.END;
        };
    }
}
