package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZClientTick extends IZetaPlayEvent {

    interface Start extends ZClientTick {
    }

    interface End extends ZClientTick {
    }
}
