package org.violetmoon.zeta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make a mixin depend on conditions to work
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalMixin {
    /**
     * List of mod id's
     */
    String[] value();

    boolean applyIfPresent() default true;
}
