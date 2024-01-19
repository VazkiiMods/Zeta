package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface AccessorCriteriaTriggers {
    @Invoker("register")
    static <T extends CriterionTrigger<?>> T zeta$register(T criterion) {
        throw new AssertionError();
    };
}
