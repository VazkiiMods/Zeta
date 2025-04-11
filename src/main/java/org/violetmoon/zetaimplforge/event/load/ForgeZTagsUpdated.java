package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.violetmoon.zeta.event.load.ZTagsUpdated;

public record ForgeZTagsUpdated(TagsUpdatedEvent event) implements ZTagsUpdated {

    @Override
    public RegistryAccess getRegistryAccess() {
        return event.getRegistryAccess();
    }

    @Override
    public boolean isOnClient() {
        return event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED;
    }
}
