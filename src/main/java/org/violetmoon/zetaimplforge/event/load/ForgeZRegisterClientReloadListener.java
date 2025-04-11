package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.violetmoon.zeta.client.event.load.ZRegisterClientReloadListener;

public record ForgeZRegisterClientReloadListener(RegisterClientReloadListenersEvent event) implements ZRegisterClientReloadListener {

    @Override
    public void accept(PreparableReloadListener bleh) {
        event.registerReloadListener(bleh);
    }
}
