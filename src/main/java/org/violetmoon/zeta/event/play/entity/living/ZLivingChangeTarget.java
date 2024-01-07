package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

public interface ZLivingChangeTarget extends IZetaPlayEvent, Cancellable, LivingGetter {
    LivingEntity getNewTarget();
    LivingChangeTargetEvent.ILivingTargetType getTargetType();
}
