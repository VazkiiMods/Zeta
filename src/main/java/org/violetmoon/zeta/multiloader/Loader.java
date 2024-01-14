package org.violetmoon.zeta.multiloader;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public enum Loader {
    // TODO - impl fabric once it gets xplat-ified
    FORGE/*, FABRIC*/;

    public static final Loader CURRENT = getCurrent();

    public boolean isCurrent() {
        return this == CURRENT;
    }

    public void runIfCurrent(Supplier<Runnable> run) {
        if (isCurrent())
            run.get().run();
    }

    @ApiStatus.Internal
    public static Loader getCurrent() {
        return Loader.FORGE;
    }
}
