package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import org.violetmoon.zeta.event.play.entity.living.ZBabyEntitySpawn;

public class ForgeZBabyEntitySpawn implements ZBabyEntitySpawn {
    public final BabyEntitySpawnEvent wrapped;

    public ForgeZBabyEntitySpawn(BabyEntitySpawnEvent e) {
        this.wrapped = e;
    }

    @Override
    public Mob getParentA() {
        return wrapped.getParentA();
    }

    @Override
    public Mob getParentB() {
        return wrapped.getParentB();
    }

    @Override
    public Player getCausedByPlayer() {
        return wrapped.getCausedByPlayer();
    }

    @Override
    public AgeableMob getChild() {
        return wrapped.getChild();
    }

    @Override
    public void setChild(AgeableMob proposedChild) {
        wrapped.setChild(proposedChild);
    }

    public static class Lowest extends ForgeZBabyEntitySpawn implements ZBabyEntitySpawn.Lowest {
        public Lowest(BabyEntitySpawnEvent wrapped) {
            super(wrapped);
        }
    }
}
