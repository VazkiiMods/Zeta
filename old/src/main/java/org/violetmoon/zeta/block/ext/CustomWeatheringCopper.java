package org.violetmoon.zeta.block.ext;

import java.util.Optional;

import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomWeatheringCopper extends WeatheringCopper {
	default BlockState getFirst(BlockState state) {
		return WeatheringCopper.getFirst(state.getBlock()).withPropertiesOf(state);
	}

	default Optional<BlockState> getPrevious(BlockState state) {
		return WeatheringCopper.getNext(state.getBlock()).map((prevBlock) -> prevBlock.withPropertiesOf(state));
	}
}
