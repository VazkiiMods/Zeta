package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface AccessorPotionBrewing {
	@Accessor("POTION_MIXES")
	static List<PotionBrewing.Mix<Potion>> zeta$getPotionMixes() {
		throw new AssertionError();
	}

}
