package org.violetmoon.zeta.util.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class UtilsImpl {
    public static boolean isModLoaded(String id, @Nullable String fabricId) {
        return ModList.get().isLoaded(id);
    }

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
