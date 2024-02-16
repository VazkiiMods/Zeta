package org.violetmoon.zeta.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

public class ForgeRaytracingUtil extends RaytracingUtil {
	@Override
	public double getEntityRange(LivingEntity player) {
		return player.getAttribute(ForgeMod.ENTITY_REACH.get()).getValue();
	}
}
