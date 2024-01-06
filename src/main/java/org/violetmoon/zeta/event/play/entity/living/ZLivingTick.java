package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.LivingEntity;

public interface ZLivingTick extends IZetaPlayEvent {
	LivingEntity getEntity();
}
