package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemUsedOnLocationTrigger.class)
public interface AccessorItemUsedOnLocationTrigger {
    @Mixin(TriggerInstance.class)
    interface AccessorTriggerInstance {
        @Accessor("location")
        ContextAwarePredicate zeta$getLocation();
    }
}
