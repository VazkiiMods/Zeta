package org.violetmoon.zeta.util.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.violetmoon.zeta.util.TransferUtil;

public class MiscUtilImpl {
    // todo Test this shit I 100% guarantee this has issues
    public static ItemStack putIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
        try (Transaction t = TransferUtil.getTransaction()) {
            if (level instanceof Level world) {
                ItemVariant stackVariant = ItemVariant.of(stack);
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, blockPos, face);
                storage.extract(stackVariant, stack.getCount(), t);
            }
        }

        return stack;
    }
}
