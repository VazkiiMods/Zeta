/package org.violetmoon.zeta.forgeapi.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Equivalent to {@link Function}, except with nonnull contract.
 *
 * @see Function
 */
@FunctionalInterface
public interface NonNullFunction<T, R> {
    @NotNull
    R apply(@NotNull T t);
}
