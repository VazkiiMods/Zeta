package org.violetmoon.zeta.client.event.load;

import java.util.function.Consumer;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ZRegisterClientReloadListener extends IZetaLoadEvent, Consumer<PreparableReloadListener> {
	@Override
	void accept(PreparableReloadListener bleh);
}
