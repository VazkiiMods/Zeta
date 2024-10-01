package org.violetmoon.zeta.item.ext;

import net.minecraft.world.item.ItemStack;

public interface ItemExtensionFactory {

	IZetaItemExtensions getInternal(ItemStack stack);

	default IZetaItemExtensions get(ItemStack stack) {
		return stack.getItem() instanceof IZetaItemExtensions ext ? ext : getInternal(stack);
	}
}