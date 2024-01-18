package org.violetmoon.zeta.util.zetalist;

import java.util.HashMap;
import java.util.Map;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ZetaClient;

public class ZetaClientList extends ZetaList<ZetaClient> {
	
	public static ZetaClientList INSTANCE = new ZetaClientList();
	
	private Map<Zeta, ZetaClient> zetasClientMap = new HashMap<>();
	
	@Override
	public void register(ZetaClient z) {
		super.register(z);
		zetasClientMap.put(z.zeta, z);
	}
	
	public ZetaClient getClientForZeta(Zeta z) {
		return zetasClientMap.get(z);
	}

}
