package org.violetmoon.zeta.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.zeta.piston.ZetaPistonStructureResolver;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
	@ModifyExpressionValue(method = {"checkIfExtend", "moveBlocks"}, at = @At(value = "NEW", target = "net/minecraft/world/level/block/piston/PistonStructureResolver"))
	private PistonStructureResolver transformStructureHelper(PistonStructureResolver prev) {
		return new ZetaPistonStructureResolver(prev);
	}
}
