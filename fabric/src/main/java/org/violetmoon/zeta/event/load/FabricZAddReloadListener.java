package org.violetmoon.zeta.event.load;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;

public class FabricZAddReloadListener implements ZAddReloadListener {
	private final AddReloadListenerEvent e;

	public FabricZAddReloadListener(AddReloadListenerEvent e) {
		this.e = e;
	}

	@Override
	public void addListener(PreparableReloadListener listener) {e.addListener(listener);}

	@Override
	public ReloadableServerResources getServerResources() {return e.getServerResources();}

	@Override
	public RegistryAccess getRegistryAccess() { return e.getRegistryAccess(); }
}
