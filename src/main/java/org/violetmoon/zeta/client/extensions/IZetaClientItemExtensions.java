package org.violetmoon.zeta.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;

/**
 * Essentially our clone of Forge's ClientItemExtensions. Implementing things from the old IZetaForgeItemStuff for now but may be expanded.
 */
public interface IZetaClientItemExtensions {
    /**
     * Gives the specific BEWLR from what we had. Useful for when it's an item.
     * @return The BEWLR of the item, or if not implemented the default BEWLR.
     */
    default BlockEntityWithoutLevelRenderer getBEWLR() {
        return Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer();
    }

    /**
     * Adds rendering support for when it's a humanoid armor model. (Like on an armor stand or player)
     * @return The HumanoidArmorModelGetter, or just null if not implemented.
     */
    default HumanoidArmorModelGetter getHumanoidArmorModel() {
        return null;
    }
}
