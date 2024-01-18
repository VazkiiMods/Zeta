package org.violetmoon.zetaimplforge.client;

import org.violetmoon.zeta.client.HumanoidArmorModelGetter;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

// initializeClient but less bad
public interface IZetaForgeItemStuff {
	void zeta$setBlockEntityWithoutLevelRenderer(BlockEntityWithoutLevelRenderer bewlr);
	void zeta$setHumanoidArmorModel(HumanoidArmorModelGetter getter);
}
