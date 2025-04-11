package org.violetmoon.zeta.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// populates a public static field in your module class with the module instance. Needs to be inside a @ZetaLoadModule class
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModuleInstance {

}
