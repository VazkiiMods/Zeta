package org.violetmoon.zetaimplforge.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Hideous marker interface used to denote Zeta specific events that are NOT supposed to be remapped to forge events
@Retention(RetentionPolicy.RUNTIME)
public @interface NotAForgeWrapper {
}
