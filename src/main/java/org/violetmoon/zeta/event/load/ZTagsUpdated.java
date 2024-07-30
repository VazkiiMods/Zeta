package org.violetmoon.zeta.event.load;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

//TODO: just used by RecipeCrawlHandler
public interface ZTagsUpdated extends IZetaLoadEvent {

    RegistryAccess getRegistryAccess();

    boolean isOnClient();

}
