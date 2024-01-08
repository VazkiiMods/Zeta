package org.violetmoon.zeta.util;

import org.violetmoon.zeta.Zeta;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ZetaEffect extends MobEffect {
	public ZetaEffect(Zeta zeta, String name, MobEffectCategory type, int color) {
		super(type, color);

		zeta.registry.register(this, name, Registries.MOB_EFFECT);
	}
}
