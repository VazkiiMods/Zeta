package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public interface ZBabyEntitySpawn extends IZetaPlayEvent {
    Mob getParentA();
    Mob getParentB();
    Player getCausedByPlayer();
    AgeableMob getChild();
    void setChild(AgeableMob proposedChild);

    interface Lowest extends ZBabyEntitySpawn { }
}
