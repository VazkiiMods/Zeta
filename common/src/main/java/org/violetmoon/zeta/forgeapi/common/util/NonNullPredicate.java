package org.violetmoon.zeta.forgeapi.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Equivalent to {@link Predicate}, except with nonnull contract.
 *
 * @see Predicate
 */
@FunctionalInterface
public interface NonNullPredicate<T> {
    boolean test(@NotNull T t);
}

