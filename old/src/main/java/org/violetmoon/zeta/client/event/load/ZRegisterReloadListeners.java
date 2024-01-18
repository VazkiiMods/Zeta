package org.violetmoon.zeta.client.event.load;

import java.util.function.Consumer;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.server.packs.resources.PreparableReloadListener;

public record ZRegisterReloadListeners(Consumer<PreparableReloadListener> manager) implements IZetaLoadEvent, Consumer<PreparableReloadListener> {
	@Override
	public void accept(PreparableReloadListener bleh) {
		manager.accept(bleh);
	}
}
