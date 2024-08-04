package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.load.ZGatherAdditionalFlags;

public class ForgeZGatherAdditionalFlags extends Event implements ZGatherAdditionalFlags {

    private final ZGatherAdditionalFlags wrapped;

    public ForgeZGatherAdditionalFlags(ZGatherAdditionalFlags wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ConfigFlagManager flagManager() {
        return wrapped.flagManager();
    }
}
