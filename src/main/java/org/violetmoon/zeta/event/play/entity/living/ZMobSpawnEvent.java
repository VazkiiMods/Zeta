package org.violetmoon.zeta.event.play.entity.living;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;

public interface ZMobSpawnEvent extends IZetaPlayEvent /*, Resultable*/ {
    Mob getEntity();
    ServerLevelAccessor getLevel();
    double getX();
    double getY();
    double getZ();
    interface CheckSpawn extends ZMobSpawnEvent {
        Either<BlockEntity, Entity> getSpawner();
        MobSpawnType getSpawnType();

        interface Lowest extends CheckSpawn { }
    }
}
