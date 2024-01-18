package org.violetmoon.zeta.block;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface SimpleFluidloggedBlock extends BucketPickup, LiquidBlockContainer {
	
	@Override
	default boolean canPlaceLiquid(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluid) {
		return fluidContained(state) == Fluids.EMPTY && acceptsFluid(fluid);
	}

	@Override
	default boolean placeLiquid(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluid) {
		if (canPlaceLiquid(level, pos, state, fluid.getType())) {
			if (!level.isClientSide()) {
				level.setBlock(pos, withFluid(state, fluid.getType()), 3);
				level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
			}

			return true;
		} else
			return false;
	}

	@NotNull
	@Override
	default ItemStack pickupBlock(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
		Fluid fluid = fluidContained(state);
		if (fluid != Fluids.EMPTY && fluid.getBucket() != Items.AIR) {
			level.setBlock(pos, withFluid(state, Fluids.EMPTY), 3);
			if (!state.canSurvive(level, pos))
				level.destroyBlock(pos, true);

			return new ItemStack(fluid.getBucket());
		} else
			return ItemStack.EMPTY;
	}

	@NotNull
	@Override
	default Optional<SoundEvent> getPickupSound() {
		return Optional.empty(); // Irrelevant - using state variant below
	}

	@Override
	default Optional<SoundEvent> getPickupSound(BlockState state) {
		return fluidContained(state).getPickupSound();
	}

	boolean acceptsFluid(@NotNull Fluid fluid);

	@NotNull
	BlockState withFluid(@NotNull BlockState state, @NotNull Fluid fluid);

	@NotNull
	Fluid fluidContained(@NotNull BlockState state);

}
