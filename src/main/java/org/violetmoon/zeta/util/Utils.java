package org.violetmoon.zeta.util;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class Utils {
    public static Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path modsDir() {
        return FMLPaths.MODSDIR.get();
    }

    public static boolean isDevEnv() {
        return !FMLLoader.isProduction();
    }
}
