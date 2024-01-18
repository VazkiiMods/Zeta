package org.violetmoon.zeta.fabric;

import org.violetmoon.zeta.Zeta;
import net.fabricmc.api.ModInitializer;

public class ZetaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Zeta.init();
    }
}