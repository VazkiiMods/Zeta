package org.violetmoon.zeta.util.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MiscUtilImpl {
    // todo Test this shit I 100% guarantee this has issues
    public static ItemStack putIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
        try (Transaction tx = Transaction.openOuter()) {
            if (level instanceof Level world) {
                ItemVariant stackVariant = ItemVariant.of(stack);
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, blockPos, face);
                storage.extract(stackVariant, stack.getCount(), tx);
            }
        }

        return stack;
    }
}
