package org.violetmoon.zetaimplforge.mixin.mixins;

import net.neoforged.bus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Event.class)
public interface AccessorEvent {

    @Accessor("isCanceled")
    boolean zeta$isCanceled();

    @Accessor("isCanceled")
    void zeta$setCanceled(boolean canceled);
}
