package org.violetmoon.zeta.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate something with @Hint to add an information page to JEI.
 * The Hint annotation will only appear when the module is enabled.
 *
 * You may annotate:
 * <ul>
 *   <li>ItemLike objects (blocks or items)
 *   <li>TagKeys containing ItemLikes
 *   <li>Iterables containing ItemLikes
 * </ul>
 *
 * @see org.violetmoon.zeta.event.play.loading.ZGatherHints
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hint {

	/**
	 * Flag value to check before applying this Hint
	 */
	String value() default "";
	boolean negate() default false;

	/**
	 * Translation key
	 */
	String key() default "";

	/**
	 * These fields will be read and put into the translation; reference them with %s
	 */
	String[] content() default "";

}
