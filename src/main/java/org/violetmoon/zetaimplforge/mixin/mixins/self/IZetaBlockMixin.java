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
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.block.ext.IZetaBlockExtensions;

@Mixin(IZetaBlock.class)
public interface IZetaBlockMixin extends IZetaBlockExtensions, IBlockExtension {
    @Override
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return getLightEmissionZeta(state, level, pos);
    }

    @Override
    default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isLadderZeta(state, level, pos, entity);
    }

    @Override
    default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
        return makesOpenTrapdoorAboveClimbableZeta(state, level, pos, trapdoorState);
    }

    @Override
    default TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        return canSustainPlantZeta(state, level, soilPosition, facing, plant);
    }

    @Override
    default boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return isConduitFrameZeta(state, level, pos, conduit);
    }

    @Override
    default float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return getEnchantPowerBonusZeta(state, level, pos);
    }

    @Override
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return getSoundTypeZeta(state, level, pos, entity);
    }

    @Override
    default @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return getBeaconColorMultiplierZeta(state, level, pos, beaconPos);
    }

    @Override
    default boolean isStickyBlock(BlockState state) {
        return isStickyBlockZeta(state);
    }

    @Override
    default boolean canStickTo(BlockState state, BlockState other) {
        return canStickToZeta(state, other);
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getFlammabilityZeta(state, level, pos, direction);
    }

    @Override
    default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammableZeta(state, level, pos, direction);
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getFireSpreadSpeedZeta(state, level, pos, direction);
    }

    @Override
    default boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
        return collisionExtendsVerticallyZeta(state, level, pos, collidingEntity);
    }

    @Override
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return shouldDisplayFluidOverlayZeta(state, level, pos, fluidState);
    }

    @Override
    default @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        return getToolModifiedStateZeta(state, context, itemAbility, simulate);
    }

    @Override
    default boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isScaffoldingZeta(state, level, pos, entity);
    }
}
