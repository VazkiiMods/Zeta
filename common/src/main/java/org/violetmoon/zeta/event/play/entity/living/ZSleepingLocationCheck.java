package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.Resultable;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

import net.minecraft.core.BlockPos;

public interface ZSleepingLocationCheck extends IZetaPlayEvent, LivingGetter, Resultable {
    BlockPos getSleepingLocation();
}
