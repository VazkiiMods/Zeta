package org.violetmoon.zetaimplforge.mod;

import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.client.ForgeZetaClient;

public class ZetaModClientForge {
    static {
        if (ZetaModForge.ZETA.side == ZetaSide.SERVER)
            throw new IllegalAccessError("SOMEONE LOADED ZetaModClientForge ON THE SERVER!!!! DON'T DO THAT!!!!!!");
    }

    public static final ZetaClient ZETA_CLIENT = new ForgeZetaClient(ZetaModForge.ZETA);

}
