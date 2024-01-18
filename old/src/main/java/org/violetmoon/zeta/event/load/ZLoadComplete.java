package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZLoadComplete extends IZetaLoadEvent {
	void enqueueWork(Runnable run);
}
