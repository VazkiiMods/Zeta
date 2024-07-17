package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IItemStackExtension.class)
public interface InvokerIItemStackExtension {

    @Invoker("self")
    ItemStack zeta$getSelf();
}
