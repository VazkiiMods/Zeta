package org.violetmoon.zeta.forgeapi.common.util;

import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

/**
 * Equivalent to {@link Consumer}, except with nonnull contract.
 *
 * @see Consumer
 */
@FunctionalInterface
public interface NonNullConsumer<T> {
    void accept(@NotNull T t);
}
