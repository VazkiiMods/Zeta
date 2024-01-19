package org.violetmoon.zeta.client.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZClientSetup extends IZetaLoadEvent {
	void enqueueWork(Runnable run);
}
