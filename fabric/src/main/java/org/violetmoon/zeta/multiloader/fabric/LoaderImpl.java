package org.violetmoon.zeta.multiloader.fabric;

import org.violetmoon.zeta.multiloader.Loader;

public class LoaderImpl {
    public static Loader getCurrent() {
        return Loader.FABRIC;
    }
}
