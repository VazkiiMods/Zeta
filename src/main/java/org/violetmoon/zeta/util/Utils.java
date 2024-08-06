package org.violetmoon.zeta.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import java.nio.file.Path;

//TODO: either move all this stuff in Zeta o move all Zeta related stuff here (like isModLoaded, client ticker and stuff)
// Either remove or move zeta helper stuff here.Forge specific behavior can be done with Platform delegates
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

    // modloader services
    public boolean isModLoaded(String modid){
        return ModList.get().isLoaded(modid);
    }

    public @Nullable String getModDisplayName(String modid){
        return ModList.get().getModContainerById(modid).map(container -> container.getModInfo().getDisplayName()).orElse(null);
    }
}
