package org.violetmoon.zetaimplforge.event.play;

import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.violetmoon.zeta.event.play.ZServerTick;

import net.minecraft.server.MinecraftServer;

public class ForgeZServerTick implements ZServerTick {
    private final ServerTickEvent e;

    public ForgeZServerTick(ServerTickEvent e) {
        this.e = e;
    }

    @Override
    public MinecraftServer getServer() {
        return e.getServer();
    }

    public static class Pre extends ForgeZServerTick implements ZServerTick.Start {
        public Pre(ServerTickEvent e) {
            super(e);
        }
    }

    public static class Post extends ForgeZServerTick implements ZServerTick.End {
        public Post(ServerTickEvent e) {
            super(e);
        }
    }
}
