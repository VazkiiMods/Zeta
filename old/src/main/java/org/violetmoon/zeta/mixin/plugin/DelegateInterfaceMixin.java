package org.violetmoon.zeta.mixin.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a mixin to delegate return value replacing from the specified class.
 *
 * DOES NOT SUPPORT REFMAPPING.
 *
 * Do not use unless you fully understand this. Please.
 *
 * - Wire
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelegateInterfaceMixin {
	Class<?> delegate();

	DelegateReturnValueModifier[] methods();
}
