package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.ZPhase;

public interface ZClientTick extends IZetaPlayEvent {
    ZPhase getPhase();
}
