package org.violetmoon.zeta.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.violetmoon.zeta.Zeta;

public class ZetaEffect extends MobEffect {
	public ZetaEffect(Zeta zeta, String name, MobEffectCategory type, int color) {
		super(type, color);

		zeta.registry.register(this, name, Registries.MOB_EFFECT);
	}
}
