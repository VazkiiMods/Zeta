package org.violetmoon.zeta.mixin.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method is a valid target for {@link DelegateReturnValueModifier}. Sugar.
 *
 * Do not use unless you fully understand this. Please.
 *
 * - Wire
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DelegateReturnValueTarget {
	String[] value();
}
