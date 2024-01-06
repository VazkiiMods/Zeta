package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import org.violetmoon.zeta.event.play.ZServerTick;

public class ForgeZServerTick implements ZServerTick {
    private final TickEvent.ServerTickEvent e;

    public ForgeZServerTick(TickEvent.ServerTickEvent e) {
        this.e = e;
    }

    @Override
    public MinecraftServer getServer() {
        return e.getServer();
    }

    public static class Start extends ForgeZServerTick implements ZServerTick.Start {
        public Start(TickEvent.ServerTickEvent e) {
            super(e);
        }
    }

    public static class End extends ForgeZServerTick implements ZServerTick.End {
        public End(TickEvent.ServerTickEvent e) {
            super(e);
        }
    }
}
