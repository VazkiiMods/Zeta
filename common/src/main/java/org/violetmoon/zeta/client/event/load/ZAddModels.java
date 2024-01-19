package org.violetmoon.zeta.client.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.resources.ResourceLocation;

public interface ZAddModels extends IZetaLoadEvent {
	void register(ResourceLocation model);
}
