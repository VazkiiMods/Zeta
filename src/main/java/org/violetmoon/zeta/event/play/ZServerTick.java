package org.violetmoon.zeta.event.play;

import net.minecraft.server.MinecraftServer;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZServerTick extends IZetaPlayEvent {
    MinecraftServer getServer();

    interface Start extends ZServerTick { }
    interface End extends ZServerTick { }
}
