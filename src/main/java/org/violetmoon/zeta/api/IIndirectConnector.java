package org.violetmoon.zeta.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public interface IIndirectConnector {

	List<Pair<Predicate<BlockState>, IIndirectConnector>> INDIRECT_STICKY_BLOCKS = new LinkedList<>();

	default boolean isEnabled() {
		return true;
	}

	default IConditionalSticky getStickyCondition() {
		return (w, pp, op, sp, os, ss, d) -> canConnectIndirectly(w, op, sp, os, ss);
	}

	boolean canConnectIndirectly(Level world, BlockPos ourPos, BlockPos sourcePos, BlockState ourState, BlockState sourceState);

}
