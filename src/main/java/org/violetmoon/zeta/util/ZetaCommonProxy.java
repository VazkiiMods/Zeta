package org.violetmoon.zeta.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.violetmoon.zeta.Zeta;

public class ZetaCommonProxy {
	public ZetaCommonProxy(Zeta zeta) {
		this.zeta = zeta;
	}

	protected Zeta zeta;

	/**
	 * Try and execute something on "the main thread". On the physical client this is well-defined (the render thread),
	 * but on the physical server... make a best-effort to use the server thread, and otherwise yolo
	 */
	public void tryToExecuteOnMainThread(Runnable runnable) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server != null)
			server.execute(runnable);
		else {
			zeta.log.warn("Using thread '{}' instead of the server thread", Thread.currentThread().getName());
			runnable.run();
		}
	}
}
