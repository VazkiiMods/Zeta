package org.violetmoon.zeta.client;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
	protected final BlockEntity be;

	public SimpleWithoutLevelRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet ems, BlockEntityType<?> beType, BlockState state) {
		super(dispatcher, ems);
		this.be = beType.create(BlockPos.ZERO, state);
	}

	public SimpleWithoutLevelRenderer(BlockEntityType<?> beType, BlockState state) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), beType, state);
	}

	@Override
	public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext itemDisplayContext, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int x, int y) {
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(be, pose, buffer, x, y);
	}
}
