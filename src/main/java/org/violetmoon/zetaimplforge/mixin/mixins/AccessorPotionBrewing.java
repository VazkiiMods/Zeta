package org.violetmoon.zetaimplforge.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

@Mixin(PotionBrewing.class)
public interface AccessorPotionBrewing {

	@Accessor
	List<PotionBrewing.Mix<Potion>> getPotionMixes();
}
