package org.violetmoon.zeta.mixin.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this class is a valid delegate for {@link DelegateInterfaceMixin}. Sugar.
 *
 * Do not use unless you fully understand this. Please.
 *
 * - Wire
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DelegateInterfaceTarget {
}
