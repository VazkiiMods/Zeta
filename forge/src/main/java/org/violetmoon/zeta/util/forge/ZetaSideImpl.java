package org.violetmoon.zeta.util.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.violetmoon.zeta.util.ZetaSide;

public class ZetaSideImpl {
    @org.jetbrains.annotations.ApiStatus.Internal
    public static ZetaSide getCurrent() {
        return FMLEnvironment.dist == Dist.CLIENT ? ZetaSide.CLIENT : ZetaSide.SERVER;
    }
}
