/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [10/01/2016, 15:13:46 (GMT)]
 */
package org.violetmoon.zeta.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.block.be.ZetaBlockEntity;

// formerly from AutoRegLib
public abstract class SimpleInventoryBlockEntity extends ZetaBlockEntity implements WorldlyContainer {

	protected NonNullList<ItemStack> inventorySlots = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

	public SimpleInventoryBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	@Override
	public void readSharedNBT(CompoundTag tag, HolderLookup.Provider provider) {
		if (!needsToSyncInventory()) return;
		ContainerHelper.loadAllItems(tag, this.inventorySlots, provider);
	}

	@Override
	public void writeSharedNBT(CompoundTag tag, HolderLookup.Provider provider) {
		if (!needsToSyncInventory()) return;
		ContainerHelper.saveAllItems(tag, this.inventorySlots, provider);
	}

	protected boolean needsToSyncInventory() {
		return true;
	}

	@NotNull
	@Override
	public ItemStack getItem(int i) {
		return inventorySlots.get(i);
	}

	@NotNull
	@Override
	public ItemStack removeItem(int i, int j) {
		if(!inventorySlots.get(i).isEmpty()) {
			ItemStack stackAt;

			if (inventorySlots.get(i).getCount() <= j) {
				stackAt = inventorySlots.get(i);
				inventorySlots.set(i, ItemStack.EMPTY);
            } else {
				stackAt = inventorySlots.get(i).split(j);

				if (inventorySlots.get(i).getCount() == 0)
					inventorySlots.set(i, ItemStack.EMPTY);
            }

            inventoryChanged(i);
            return stackAt;
        }

		return ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack removeItemNoUpdate(int i) {
		ItemStack stack = getItem(i);
		setItem(i, ItemStack.EMPTY);
		inventoryChanged(i);
		return stack;
	}

	@Override
	public void setItem(int i, @NotNull ItemStack itemstack) {
		inventorySlots.set(i, itemstack);
		inventoryChanged(i);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			if(!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean stillValid(@NotNull Player entityplayer) {
		return getLevel().getBlockEntity(getBlockPos()) == this && entityplayer.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64;
	}

	// TODO: REMOVE?
	/*
	@SuppressWarnings("unchecked")
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return (LazyOptional<T>)) LazyOptional.of(() -> new SidedInvWrapper(this, facing));

		return LazyOptional.empty();
	}
	 */

	@Override
	public boolean canPlaceItem(int i, @NotNull ItemStack itemstack) {
		return true;
	}

	@Override
	public abstract void startOpen(@NotNull Player player);

	@Override
	public abstract void stopOpen(@NotNull Player player);

	@Override
	public void clearContent() {
		inventorySlots = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
	}

	abstract void inventoryChanged(int i);

	public boolean isAutomationEnabled() {
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
		return isAutomationEnabled();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, Direction direction) {
		return isAutomationEnabled();
	}

	@Override
	public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
		if(isAutomationEnabled()) {
			int[] slots = new int[getContainerSize()];
			for(int i = 0; i < slots.length; i++)
				slots[i] = i;
			return slots;
		}

		return new int[0];
	}
}
