package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.Entity;

public interface ZEntityTeleport extends IZetaPlayEvent {
    Entity getEntity();
    double getTargetX();
    double getTargetY();
    double getTargetZ();
    void setTargetX(double targetX);
    void setTargetY(double targetY);
    void setTargetZ(double targetZ);
}
