package org.violetmoon.zeta.registry;

import org.jetbrains.annotations.Nullable;

public interface IZetaBlockColorProvider extends IZetaItemColorProvider {
	@Nullable String getBlockColorProviderName();
}
