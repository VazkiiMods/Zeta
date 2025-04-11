package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingChangeTarget;

public record ForgeZLivingChangeTarget(LivingChangeTargetEvent e) implements ZLivingChangeTarget {

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    @Override
    public LivingEntity getNewTarget() {
        return e.getNewAboutToBeSetTarget();
    }

    @Override
    public LivingChangeTargetEvent.ILivingTargetType getTargetType() {
        return e.getTargetType();
    }

    @Override
    public boolean isCanceled() {
        return e.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        e.setCanceled(cancel);
    }
}
