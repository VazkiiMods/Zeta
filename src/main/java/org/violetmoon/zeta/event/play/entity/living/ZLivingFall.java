package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

public interface ZLivingFall extends IZetaPlayEvent, LivingGetter {
    float getDistance();
    void setDistance(float distance);
}
