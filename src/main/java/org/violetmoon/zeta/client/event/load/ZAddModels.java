package org.violetmoon.zeta.client.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZAddModels extends IZetaLoadEvent {
	void register(ModelResourceLocation model);
}
