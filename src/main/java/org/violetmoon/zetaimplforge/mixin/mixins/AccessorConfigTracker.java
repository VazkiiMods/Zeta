package org.violetmoon.zetaimplforge.mixin.mixins;

import net.neoforged.fml.config.ConfigTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(ConfigTracker.class)
public interface AccessorConfigTracker {

    @Accessor("locksByMod")
    Map<String, ReentrantLock> zeta$getLocksByMod();
}
