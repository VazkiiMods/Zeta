package org.violetmoon.zeta.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.server.MinecraftServer;

public interface ZServerTick extends IZetaPlayEvent {
    MinecraftServer getServer();

    interface Start extends ZServerTick { }
    interface End extends ZServerTick { }
}
