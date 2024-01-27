package org.violetmoon.zeta.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.violetmoon.zeta.util.ZetaSide;

public class ZetaSideImpl {
    @org.jetbrains.annotations.ApiStatus.Internal
    public static ZetaSide getCurrent() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ZetaSide.CLIENT : ZetaSide.SERVER;
    }
}
