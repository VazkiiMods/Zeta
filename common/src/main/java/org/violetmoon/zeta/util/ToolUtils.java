package org.violetmoon.zeta.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.mixin.mixins.AccessorAxeItem;
import org.violetmoon.zeta.mixin.mixins.AccessorShovelItem;

public class ToolUtils {
    public static @NotNull BlockState getAxeStrippingState(BlockState originalState) {
        Block block = AccessorAxeItem.zeta$getStrippables().get(originalState.getBlock());
        return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)) : null;
    }

    public static BlockState getShovelPathingState(BlockState originalState) {
        return AccessorShovelItem.zeta$getFlattenables().get(originalState.getBlock());
    }
}
