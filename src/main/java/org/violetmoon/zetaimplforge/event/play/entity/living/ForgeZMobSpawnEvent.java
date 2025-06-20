package org.violetmoon.zetaimplforge.event.play.entity.living;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.play.entity.living.ZMobSpawnEvent;

public class ForgeZMobSpawnEvent implements ZMobSpawnEvent {
    public final MobSpawnEvent e;

    public ForgeZMobSpawnEvent(MobSpawnEvent e) {
        this.e = e;
    }

    @Override
    public Mob getEntity() {
        return e.getEntity();
    }

    @Override
    public ServerLevelAccessor getLevel() {
        return e.getLevel();
    }

    @Override
    public double getX() {
        return e.getX();
    }

    @Override
    public double getY() {
        return e.getY();
    }

    @Override
    public double getZ() {
        return e.getZ();
    }

    /*@Override
    public ZResult getResult() {
        return ForgeZeta.from(e.getResult());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(ForgeZeta.to(value));
    }*/

    public static class FinalizeSpawn extends ForgeZMobSpawnEvent implements ZMobSpawnEvent.CheckSpawn {
        public final FinalizeSpawnEvent e;

        public FinalizeSpawn(FinalizeSpawnEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public Either<BlockEntity, Entity> getSpawner() {
            return e.getSpawner();
        }

        @Override
        public MobSpawnType getSpawnType() {
            return e.getSpawnType();
        }

        @Override
        public boolean getResult() {
            return !e.isSpawnCancelled();
        }

        @Override
        public void setResult(ZResult value) {
            e.setSpawnCancelled(!value.equals(ZResult.DENY));
        }

        public static class Lowest extends FinalizeSpawn implements ZMobSpawnEvent.CheckSpawn.Lowest {
            public Lowest(FinalizeSpawnEvent e) {
                super(e);
            }
        }
    }
}
