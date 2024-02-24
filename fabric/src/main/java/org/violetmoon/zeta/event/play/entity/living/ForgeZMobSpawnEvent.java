package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.violetmoon.zeta.FabricZeta;
import org.violetmoon.zeta.event.bus.ZResult;

public class ForgeZMobSpawnEvent implements ZMobSpawnEvent {
    private final MobSpawnEvent e;

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

    @Override
    public ZResult getResult() {
        return FabricZeta.from(e.getResult());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(FabricZeta.to(value));
    }

    public static class FinalizeSpawn extends ForgeZMobSpawnEvent implements CheckSpawn {
        private final MobSpawnEvent.FinalizeSpawn e;

        public FinalizeSpawn(MobSpawnEvent.FinalizeSpawn e) {
            super(e);
            this.e = e;
        }

        @Override
        public BaseSpawner getSpawner() {
            return e.getSpawner();
        }

        @Override
        public MobSpawnType getSpawnType() {
            return e.getSpawnType();
        }

        public static class Lowest extends FinalizeSpawn implements CheckSpawn.Lowest {
            public Lowest(MobSpawnEvent.FinalizeSpawn e) {
                super(e);
            }
        }
    }
}
