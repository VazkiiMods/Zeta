package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZGatherAdditionalFlags extends IZetaLoadEvent {

    ConfigFlagManager flagManager();
}
