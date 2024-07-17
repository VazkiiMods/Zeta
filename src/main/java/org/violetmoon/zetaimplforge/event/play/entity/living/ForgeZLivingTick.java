package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingTick;

public record ForgeZLivingTick(EntityTickEvent e) implements ZLivingTick {

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}
}