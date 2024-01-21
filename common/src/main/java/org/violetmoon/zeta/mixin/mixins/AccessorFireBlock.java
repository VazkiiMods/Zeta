package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface AccessorFireBlock {
    @Invoker("getBurnOdds")
    int zeta$getBurnOdds(BlockState state);

    @Invoker("getIgniteOdds")
    int zeta$getIgniteOdds(BlockState state);
}
