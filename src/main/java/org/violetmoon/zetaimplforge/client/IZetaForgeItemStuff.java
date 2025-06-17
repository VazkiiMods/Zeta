package org.violetmoon.zetaimplforge.client;

import org.violetmoon.zeta.client.HumanoidArmorModelGetter;

// initializeClient but less bad
public interface IZetaForgeItemStuff {
	void zeta$setBlockEntityWithoutLevelRenderer(BlockEntityWithoutLevelRenderer bewlr);
	void zeta$setHumanoidArmorModel(HumanoidArmorModelGetter getter);
}
