package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Advancement.class)
public interface AccessorAdvancement {
    @Accessor("criteria")
    Map<String, Criterion> zeta$getCriteria();

    @Accessor("criteria")
    void zeta$setCriteria(Map<String, Criterion> criteria);

    @Accessor("requirements")
    String[][] zeta$getRequirements();

    @Accessor("requirements")
    void zeta$setRequirements(String[][] requirements);
}
