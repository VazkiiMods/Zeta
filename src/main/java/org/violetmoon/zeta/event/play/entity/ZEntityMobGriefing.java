package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.Entity;

public interface ZEntityMobGriefing extends IZetaPlayEvent {
    Entity getEntity();
    void setCanGrief(boolean canGrief);
    boolean canGrief();
}
