package org.violetmoon.zeta.event.load;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

//TODO: just used by RecipeCrawlHandler
public interface ZTagsUpdated extends IZetaPlayEvent {

    RegistryAccess getRegistryAccess();

    boolean isOnClient();

}
