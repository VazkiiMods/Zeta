package org.violetmoon.zeta.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import java.nio.file.Path;
import java.util.Locale;

public class Utils {
    public static boolean isModLoaded(String id) {
        return isModLoaded(id, null);
    }

    @ExpectPlatform
    public static boolean isModLoaded(String id, @Nullable String fabricId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path configDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path modsDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevEnv() {
        throw new AssertionError();
    }

    public static boolean isEnvVarTrue(String name) {
        try {
            String result = System.getenv(name);
            return result != null && result.toLowerCase(Locale.ROOT).equals("true");
        } catch (SecurityException e) {
            Zeta.GLOBAL_LOG.warn("Caught a security exception while trying to access environment variable `{}`.", name);
            return false;
        }
    }
}
