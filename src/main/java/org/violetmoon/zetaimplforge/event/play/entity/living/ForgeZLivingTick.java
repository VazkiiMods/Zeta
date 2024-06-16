package org.violetmoon.zetaimplforge.event.play.entity.living;

import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;

import net.minecraft.world.entity.LivingEntity;

public record ForgeZLivingTick(LivingEvent.LivingTickEvent e) implements ZLivingTick {
	@Override
	public LivingEntity getEntity() {
		return e.getEntity();
	}
}
