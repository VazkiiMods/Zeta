package org.violetmoon.zetaimplforge.util;

import net.minecraft.world.entity.ai.attributes.Attributes;
import org.violetmoon.zeta.util.RaytracingUtil;

import net.minecraft.world.entity.LivingEntity;

public class ForgeRaytracingUtil extends RaytracingUtil {
	@Override
	public double getEntityRange(LivingEntity player) {
		return player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue();
	}
}
