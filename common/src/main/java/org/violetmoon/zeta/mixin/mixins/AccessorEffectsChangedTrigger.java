package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EffectsChangedTrigger.class)
public interface AccessorEffectsChangedTrigger {
    @Mixin(TriggerInstance.class)
    interface AccessorTriggerInstance {
        @Accessor("effects")
        MobEffectsPredicate zeta$getEffects();
    }
}
