package org.violetmoon.zeta.event.play;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;

public class FabricZServerTick implements ZServerTick {
    private final TickEvent.ServerTickEvent e;

    public FabricZServerTick(TickEvent.ServerTickEvent e) {
        this.e = e;
    }

    @Override
    public MinecraftServer getServer() {
        return e.getServer();
    }

    public static class Start extends FabricZServerTick implements ZServerTick.Start {
        public Start(TickEvent.ServerTickEvent e) {
            super(e);
        }
    }

    public static class End extends FabricZServerTick implements ZServerTick.End {
        public End(TickEvent.ServerTickEvent e) {
            super(e);
        }
    }
}
