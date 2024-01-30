package org.violetmoon.zeta.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Requirement {
    /**
     * List of mod-id's
     */
    String[] value();

    /**
     * Versions | Doesnt work yet
     */
    String[] versionPredicates() default {};
}
