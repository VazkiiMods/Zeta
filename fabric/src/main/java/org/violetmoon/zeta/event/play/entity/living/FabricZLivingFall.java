package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FabricZLivingFall implements ZLivingFall {
    private final LivingFallEvent e;

    public FabricZLivingFall(LivingFallEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    @Override
    public float getDistance() {
        return e.getDistance();
    }

    @Override
    public void setDistance(float distance) {
        e.setDistance(distance);
    }
}
