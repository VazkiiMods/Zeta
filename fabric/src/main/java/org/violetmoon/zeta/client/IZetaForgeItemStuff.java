package org.violetmoon.zeta.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

// initializeClient but less bad
public interface IZetaForgeItemStuff {
	void zeta$setBlockEntityWithoutLevelRenderer(BlockEntityWithoutLevelRenderer bewlr);
	void zeta$setHumanoidArmorModel(HumanoidArmorModelGetter getter);
}
