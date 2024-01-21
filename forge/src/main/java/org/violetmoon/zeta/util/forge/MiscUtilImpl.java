package org.violetmoon.zeta.util.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class MiscUtilImpl {
    public static ItemStack putIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
        IItemHandler handler = null;

        if(level != null && blockPos != null && level.getBlockState(blockPos).getBlock() instanceof WorldlyContainerHolder holder) {
            handler = new SidedInvWrapper(holder.getContainer(level.getBlockState(blockPos), level, blockPos), face);
        } else if(tile != null) {
            LazyOptional<IItemHandler> opt  = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, face);
            if(opt.isPresent())
                handler = opt.orElse(new ItemStackHandler());
            else if(tile instanceof WorldlyContainer container)
                handler = new SidedInvWrapper(container, face);
            else if(tile instanceof Container container)
                handler = new InvWrapper(container);
        }

        if(handler != null)
            return (simulate && !doSimulation) ? ItemStack.EMPTY : ItemHandlerHelper.insertItem(handler, stack, simulate);

        return stack;
    }
}
