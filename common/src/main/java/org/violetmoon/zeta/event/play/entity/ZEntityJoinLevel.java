package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.Entity;

public interface ZEntityJoinLevel extends IZetaPlayEvent, Cancellable {
    Entity getEntity();
}
