package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface InvokerBlockBehavior {

    @Invoker("propagatesSkylightDown")
    boolean zeta$propogatesSkylightDown(BlockState state, BlockGetter getter, BlockPos blockPos);
}
