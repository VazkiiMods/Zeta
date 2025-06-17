package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

public interface ZRightClickBlock extends IZetaPlayEvent, Cancellable, PlayerGetter {
	Level getLevel();
	BlockPos getPos();
	InteractionHand getHand();
	ItemStack getItemStack();
	BlockHitResult getHitVec();
	Direction getFace();
	TriState getUseBlock();
	InteractionResult getCancellationResult();
	void setCancellationResult(InteractionResult result);

	interface Low extends IZetaPlayEvent, ZRightClickBlock { }
}
