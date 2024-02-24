package org.violetmoon.zeta.util;

import net.minecraft.world.entity.LivingEntity;

public class FabricRaytracingUtil extends RaytracingUtil {
	@Override
	public double getEntityRange(LivingEntity player) {
		return 4; //todo: Combine this with a reach mod lol
	}
}
