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
     * Only load mixin if requirements are fulfilled
     */
    Requirement[] require() default {};

    /**
     * Don't load mixin if any of the requirements are fulfilled, higher priority then require
     */
    Requirement[] conflict() default {};
}
