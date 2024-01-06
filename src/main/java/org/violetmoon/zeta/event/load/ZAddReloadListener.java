package org.violetmoon.zeta.event.load;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ZAddReloadListener extends IZetaLoadEvent {
	ReloadableServerResources getServerResources();
	RegistryAccess getRegistryAccess();
	void addListener(PreparableReloadListener listener);
}
