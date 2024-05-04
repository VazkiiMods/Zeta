package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.LivingEntity;

public record FabricZLivingTick(LivingEntity entity) implements ZLivingTick {
	@Override
	public LivingEntity getEntity() {
		return entity;
	}
}
