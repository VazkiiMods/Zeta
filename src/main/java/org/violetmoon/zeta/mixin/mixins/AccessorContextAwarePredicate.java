package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(ContextAwarePredicate.class)
public interface AccessorContextAwarePredicate {

    @Accessor("compositePredicates")
    Predicate<LootContext> zeta$getCompositePredicates();

    @Accessor("compositePredicates")
    void zeta$setCompositePredicates(Predicate<LootContext> predicate);
}
