package org.violetmoon.zeta.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;

@Mixin(PistonStructureResolver.class)
public interface AccessorPistonStructureResolver {

	@Accessor("extending")
	boolean zeta$extending();

	@Accessor("level")
	Level zeta$level();

	@Accessor("pistonPos")
	BlockPos zeta$pistonPos();

	@Accessor("pistonDirection")
	Direction zeta$pistonDirection();
}
