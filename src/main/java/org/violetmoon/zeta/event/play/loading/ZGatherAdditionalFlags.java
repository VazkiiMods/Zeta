package org.violetmoon.zeta.event.play.loading;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import java.util.Objects;

public interface ZGatherAdditionalFlags extends IZetaPlayEvent {

    ConfigFlagManager flagManager();
}
