package org.violetmoon.zeta.mod;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.TopLayerTooltipHandler;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.util.handler.RequiredModTooltipHandler;

public class ZetaClientProxy extends ZetaModProxy {

	public static ZetaClient ZETA_CLIENT;
	
	@Override
	public void registerEvents(Zeta zeta) {
		super.registerEvents(zeta);
		
		zeta.playBus
			.subscribe(TopLayerTooltipHandler.class)
			.subscribe(new RequiredModTooltipHandler.Client(zeta));
	}

	@Override
	public void setClientZeta(Object obj) {
		ZETA_CLIENT = (ZetaClient) obj;
	}
	
}
