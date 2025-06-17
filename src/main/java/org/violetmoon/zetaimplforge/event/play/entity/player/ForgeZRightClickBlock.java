package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickBlock;

public class ForgeZRightClickBlock implements ZRightClickBlock {

	private final PlayerInteractEvent.RightClickBlock e;

	public ForgeZRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		this.e = e;
	}

	@Override
	public Player getEntity() {
		return e.getEntity();
	}

	@Override
	public Level getLevel() {
		return e.getLevel();
	}

	@Override
	public BlockPos getPos() {
		return e.getPos();
	}

	@Override
	public InteractionHand getHand() {
		return e.getHand();
	}

	@Override
	public ItemStack getItemStack() {
		return e.getItemStack();
	}

	@Override
	public BlockHitResult getHitVec() {
		return e.getHitVec();
	}

	@Override
	public Direction getFace() {
		return e.getFace();
	}

	@Override
	public TriState getUseBlock() {
		return e.getUseBlock();
	}

	@Override
	public boolean isCanceled() {
		return e.isCanceled();
	}

	@Override
	public void setCanceled(boolean canceled) {
		e.setCanceled(canceled);
		if (canceled) {
			e.setUseBlock(TriState.FALSE);
			e.setUseItem(TriState.FALSE);
		}
	}

	@Override
	public InteractionResult getCancellationResult() {
		return e.getCancellationResult();
	}

	@Override
	public void setCancellationResult(InteractionResult result) {
		e.setCancellationResult(result);
	}

	public static class Low extends ForgeZRightClickBlock implements ZRightClickBlock.Low {
		public Low(PlayerInteractEvent.RightClickBlock e) {
			super(e);
		}
	}
}
