package org.violetmoon.zetaimplforge.mixin.mixins.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.block.ZetaBlockWrapper;
import org.violetmoon.zeta.block.ZetaBushBlock;
import org.violetmoon.zeta.block.ZetaButtonBlock;
import org.violetmoon.zeta.block.ZetaCeilingHangingSignBlock;
import org.violetmoon.zeta.block.ZetaDoorBlock;
import org.violetmoon.zeta.block.ZetaFenceBlock;
import org.violetmoon.zeta.block.ZetaFenceGateBlock;
import org.violetmoon.zeta.block.ZetaInheritedPaneBlock;
import org.violetmoon.zeta.block.ZetaLeavesBlock;
import org.violetmoon.zeta.block.ZetaPaneBlock;
import org.violetmoon.zeta.block.ZetaPillarBlock;
import org.violetmoon.zeta.block.ZetaPressurePlateBlock;
import org.violetmoon.zeta.block.ZetaSaplingBlock;
import org.violetmoon.zeta.block.ZetaSlabBlock;
import org.violetmoon.zeta.block.ZetaStairsBlock;
import org.violetmoon.zeta.block.ZetaStandingSignBlock;
import org.violetmoon.zeta.block.ZetaTrapdoorBlock;
import org.violetmoon.zeta.block.ZetaVineBlock;
import org.violetmoon.zeta.block.ZetaWallBlock;
import org.violetmoon.zeta.block.ZetaWallHangingSignBlock;
import org.violetmoon.zeta.block.ZetaWallSignBlock;
import org.violetmoon.zeta.block.ext.IZetaBlockExtensions;

import java.util.Locale;

// Forge can't actually mixin to interfaces, so we fake it by just... mixing in to everyone inheriting the interface.
@Mixin({
	ZetaBlock.class,
	ZetaBlockWrapper.class,
	ZetaBushBlock.class,
	ZetaButtonBlock.class,
	ZetaCeilingHangingSignBlock.class,
	ZetaDoorBlock.class,
	ZetaFenceBlock.class,
	ZetaFenceGateBlock.class,
	ZetaInheritedPaneBlock.class,
	ZetaLeavesBlock.class,
	ZetaPaneBlock.class,
	ZetaPillarBlock.class,
	ZetaPressurePlateBlock.class,
	ZetaSaplingBlock.class,
	ZetaSlabBlock.class,
	ZetaStairsBlock.class,
	ZetaStandingSignBlock.class,
	ZetaTrapdoorBlock.class,
	ZetaVineBlock.class,
	ZetaWallHangingSignBlock.class,
	ZetaWallBlock.class,
	ZetaWallSignBlock.class,
})
public class IZetaBlockMixin_FAKE implements IZetaBlockExtensions, IBlockExtension {

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return getLightEmissionZeta(state, level, pos);
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return isLadderZeta(state, level, pos, entity);
	}

	@Override
	public boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return makesOpenTrapdoorAboveClimbableZeta(state, level, pos, trapdoorState);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return canSustainPlantZeta(state, level, pos, facing, plantable.getPlantType(level, pos).getName().toLowerCase(Locale.ROOT));
	}

	@Override
	public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
		return isConduitFrameZeta(state, level, pos, conduit);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
		return getEnchantPowerBonusZeta(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return getSoundTypeZeta(state, level, pos, entity);
	}

	@Override
	public @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return getBeaconColorMultiplierZeta(state, level, pos, beaconPos);
	}

	@Override
	public boolean isStickyBlock(BlockState state) {
		return isStickyBlockZeta(state);
	}

	@Override
	public boolean canStickTo(BlockState state, BlockState other) {
		return canStickToZeta(state, other);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getFlammabilityZeta(state, level, pos, direction);
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return isFlammableZeta(state, level, pos, direction);
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getFireSpreadSpeedZeta(state, level, pos, direction);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return collisionExtendsVerticallyZeta(state, level, pos, collidingEntity);
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return shouldDisplayFluidOverlayZeta(state, level, pos, fluidState);
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		String toolActionName = toolAction.name();
		return getToolModifiedStateZeta(state, context, toolActionName, simulate);
	}

	@Override
	public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return isScaffoldingZeta(state, level, pos, entity);
	}

}
