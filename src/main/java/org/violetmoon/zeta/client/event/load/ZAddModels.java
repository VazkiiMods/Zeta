package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.resources.model.ModelResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZAddModels extends IZetaLoadEvent {
	void register(ModelResourceLocation model);
}
