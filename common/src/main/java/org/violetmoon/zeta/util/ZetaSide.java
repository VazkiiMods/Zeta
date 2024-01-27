package org.violetmoon.zeta.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.multiloader.Env;

import java.util.function.Supplier;

public enum ZetaSide {
	CLIENT, SERVER;

	public static ZetaSide fromClient(boolean isClient) {
		return isClient ? CLIENT : SERVER;
	}

	public static final ZetaSide CURRENT = getCurrent();

	public boolean isCurrent() {
		return this == CURRENT;
	}

	public void runIfCurrent(Supplier<Runnable> run) {
		if (isCurrent())
			run.get().run();
	}

	public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
		return switch (ZetaSide.CURRENT) {
			case CLIENT -> clientTarget.get().get();
			case SERVER -> serverTarget.get().get();
		};
	}

	@ApiStatus.Internal
	@ExpectPlatform
	public static ZetaSide getCurrent() {
		throw new AssertionError();
	}
}
