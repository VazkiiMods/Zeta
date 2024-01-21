package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AxeItem.class)
public interface AccessorAxeItem {
    @Accessor("STRIPPABLES")
    static Map<Block, Block> zeta$getStrippables() {
        throw new AssertionError();
    }
}
