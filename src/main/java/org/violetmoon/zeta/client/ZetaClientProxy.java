package org.violetmoon.zeta.client;

import net.minecraft.client.Minecraft;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.ZetaCommonProxy;

public class ZetaClientProxy extends ZetaCommonProxy {
	public ZetaClientProxy(Zeta zeta) {
		super(zeta);
	}

	@Override
	public void tryToExecuteOnMainThread(Runnable runnable) {
		Minecraft.getInstance().execute(runnable);
	}
}
