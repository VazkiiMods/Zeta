package org.violetmoon.zetaimplforge.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;

// initializeClient but less bad
public interface IZetaForgeItemStuff {
	void zeta$setBlockEntityWithoutLevelRenderer(BlockEntityWithoutLevelRenderer bewlr);
	void zeta$setHumanoidArmorModel(HumanoidArmorModelGetter getter);
}
