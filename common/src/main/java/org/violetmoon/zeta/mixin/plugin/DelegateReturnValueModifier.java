package org.violetmoon.zeta.mixin.plugin;

/**
 * Specifies which methods need delegation.
 *
 * {@param delegate} is the name of the method targeted.
 * {@param desc} is the descriptor of the method targeted.
 *
 * Do not use unless you fully understand this. Please.
 *
 * - Wire
 */
public @interface DelegateReturnValueModifier {
	String[] target();
	String delegate();
	String desc();
}
