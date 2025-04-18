package org.violetmoon.zeta.util.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

// put here loader specific events that need to be fired
public interface LoaderSpecificEventsHandler {

    boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr);

}
