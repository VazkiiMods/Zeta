package org.violetmoon.zetaimplforge.client.event.play;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.violetmoon.zeta.client.event.play.ZClientTick;

import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ForgeZClientTick implements ZClientTick {
    public final ClientTickEvent e;

    public ForgeZClientTick(ClientTickEvent e) {
        this.e = e;
    }

    public static class Start extends ForgeZClientTick implements ZClientTick.Start {
        public Start(ClientTickEvent e) {
            super(e);
        }
    }

    public static class End extends ForgeZClientTick implements ZClientTick.End {
        public End(ClientTickEvent e) {
            super(e);
        }
    }
}

