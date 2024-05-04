package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class FabricZBabyEntitySpawn implements ZBabyEntitySpawn {
    private final BabyEntitySpawnEvent e;

    public FabricZBabyEntitySpawn(BabyEntitySpawnEvent e) {
        this.e = e;
    }

    @Override
    public Mob getParentA() {
        return e.getParentA();
    }

    @Override
    public Mob getParentB() {
        return e.getParentB();
    }

    @Override
    public Player getCausedByPlayer() {
        return e.getCausedByPlayer();
    }

    @Override
    public AgeableMob getChild() {
        return e.getChild();
    }

    @Override
    public void setChild(AgeableMob proposedChild) {
        e.setChild(proposedChild);
    }

    public static class Lowest extends FabricZBabyEntitySpawn implements ZBabyEntitySpawn.Lowest {
        public Lowest(BabyEntitySpawnEvent e) {
            super(e);
        }
    }
}
