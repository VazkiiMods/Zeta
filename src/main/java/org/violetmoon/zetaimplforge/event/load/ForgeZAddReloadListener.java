package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.event.load.ZAddReloadListener;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;

public class ForgeZAddReloadListener implements ZAddReloadListener {
	private final AddReloadListenerEvent e;

	public ForgeZAddReloadListener(AddReloadListenerEvent e) {
		this.e = e;
	}

	@Override
	public void addListener(PreparableReloadListener listener) {e.addListener(listener);}

	@Override
	public ReloadableServerResources getServerResources() {return e.getServerResources();}

	@Override
	public RegistryAccess getRegistryAccess() { return e.getRegistryAccess(); }
}
