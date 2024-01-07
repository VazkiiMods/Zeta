package org.violetmoon.zeta.api;

import net.minecraft.advancements.Criterion;

public interface IMutableAdvancement {

    void addRequiredCriterion(String name, Criterion criterion);

    void addOrCriterion(String name, Criterion criterion);

    Criterion getCriterion(String title);
}
