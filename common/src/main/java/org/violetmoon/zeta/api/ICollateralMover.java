package org.violetmoon.zeta.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface ICollateralMover {

	default boolean isCollateralMover(Level world, BlockPos source, Direction moveDirection, BlockPos pos) {
		return true;
	}

	MoveResult getCollateralMovement(Level world, BlockPos source, Direction moveDirection, Direction side, BlockPos pos);

	enum MoveResult {

		MOVE,
		BREAK,
		SKIP,
		PREVENT

	}


}
