package org.violetmoon.zeta.multiloader.forge;

import org.violetmoon.zeta.multiloader.Loader;

public class LoaderImpl {
    public static Loader getCurrent() {
        return Loader.FORGE;
    }
}
