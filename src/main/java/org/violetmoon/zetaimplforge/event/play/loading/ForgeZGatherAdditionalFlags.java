package org.violetmoon.zetaimplforge.event.play.loading;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags;

public class ForgeZGatherAdditionalFlags extends Event implements ZGatherAdditionalFlags {
    private final ConfigFlagManager flagManager;

    public ForgeZGatherAdditionalFlags(ConfigFlagManager flagManager) {
        this.flagManager = flagManager;
    }

    @Override
    public ConfigFlagManager flagManager() {
        return flagManager;
    }
}
