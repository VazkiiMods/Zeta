package org.violetmoon.zetaimplforge.event.play.loading;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags;

@Deprecated(forRemoval = true)
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
