package org.violetmoon.zeta.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

/**
 * Assortment of Block Related Utilities
 */
public class BlockUtils {
	public static boolean isWoodBased(BlockState state) {
		NoteBlockInstrument noteBlockInstrument = state.instrument();
		SoundType soundType = state.getSoundType();

		return noteBlockInstrument == NoteBlockInstrument.BASS ||
				soundType == SoundType.BAMBOO_WOOD ||
				soundType == SoundType.CHERRY_WOOD ||
				soundType == SoundType.NETHER_WOOD ||
				soundType == SoundType.WOOD;
	}

	public static boolean isGlassBased(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) { //todo: Access Widener
		return blockState.getBlock().propagatesSkylightDown(blockState, blockGetter, blockPos) ||
				blockState.getSoundType() == SoundType.GLASS;
	}

	public static boolean isStoneBased(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
		return blockState.instrument() == NoteBlockInstrument.BASEDRUM || blockState.getMapColor(blockGetter, blockPos) == MapColor.STONE;
	}

	public static boolean canFallThrough(BlockState state) {
		Block block = state.getBlock();
		return state.isAir() || block == Blocks.FIRE || state.liquid() || state.canBeReplaced();
	}
}
