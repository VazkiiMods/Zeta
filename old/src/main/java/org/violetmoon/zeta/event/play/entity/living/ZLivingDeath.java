package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface ZLivingDeath extends IZetaPlayEvent {
	Entity getEntity();
	DamageSource getSource();

	interface Lowest extends ZLivingDeath { }
}
