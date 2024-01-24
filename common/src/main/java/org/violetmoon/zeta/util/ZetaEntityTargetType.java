package org.violetmoon.zeta.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;

public enum ZetaEntityTargetType {
    /**
     Equivalent to MOB_TARGET from Forge, fired from {@link Mob#setTarget(LivingEntity)}.
     */
    MOB_TARGET,
    /**
     Equivalent to BEHAVIOR_TARGET from Forge, fired from {@link StartAttacking}.
     */
    BEHAVIOR_TARGET,
    /**
     Anything outside of these two.
     */
    MISC_TARGET
}
