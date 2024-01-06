package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.Resultable;

public interface ZMobSpawnEvent extends IZetaPlayEvent, Resultable {
    Mob getEntity();
    ServerLevelAccessor getLevel();
    double getX();
    double getY();
    double getZ();
    interface CheckSpawn extends ZMobSpawnEvent {
        BaseSpawner getSpawner();
        MobSpawnType getSpawnType();

        interface Lowest extends CheckSpawn { }
    }
}
