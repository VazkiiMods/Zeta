package org.violetmoon.zeta.event.bus.helpers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface PlayerGetter extends LivingGetter {
	default Player getPlayer() {
		LivingEntity living = getEntity();
		return living instanceof Player p ? p : null;
	}
}