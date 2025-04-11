package org.violetmoon.zeta.event.play.loading;

import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

//use load event instead
@Deprecated(forRemoval = true)
public interface ZGatherAdditionalFlags extends IZetaPlayEvent {

    ConfigFlagManager flagManager();
}
