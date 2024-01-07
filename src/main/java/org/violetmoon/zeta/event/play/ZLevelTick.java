package org.violetmoon.zeta.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.level.Level;

public interface ZLevelTick extends IZetaPlayEvent {
    Level getLevel();

    interface Start extends ZLevelTick { }
    interface End extends ZLevelTick { }
}
