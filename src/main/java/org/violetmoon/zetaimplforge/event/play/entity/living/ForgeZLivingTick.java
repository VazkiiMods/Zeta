package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;

import net.minecraft.world.entity.LivingEntity;

public record ForgeZLivingTick(LivingTickEvent e) implements ZLivingTick {
	@Override
	public LivingEntity getEntity() {
		return e.getEntity();
	}
}
