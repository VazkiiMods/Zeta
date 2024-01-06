package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.Resultable;

import net.minecraft.world.entity.Entity;

public interface ZEntityMobGriefing extends IZetaPlayEvent, Resultable {
    Entity getEntity();
}
