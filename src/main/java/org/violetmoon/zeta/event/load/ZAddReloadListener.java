package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZAddReloadListener extends IZetaLoadEvent {
	ReloadableServerResources getServerResources();
	RegistryAccess getRegistryAccess();
	void addListener(PreparableReloadListener listener);
}
