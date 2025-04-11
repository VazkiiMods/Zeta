package org.violetmoon.zetaimplforge.event.load;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.load.ZGatherAdditionalFlags;

public class ForgeZGatherAdditionalFlags extends Event implements ZGatherAdditionalFlags , IModBusEvent {

    private final ZGatherAdditionalFlags wrapped;

    public ForgeZGatherAdditionalFlags(ZGatherAdditionalFlags wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ConfigFlagManager flagManager() {
        return wrapped.flagManager();
    }
}
