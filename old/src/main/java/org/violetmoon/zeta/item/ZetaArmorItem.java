package org.violetmoon.zeta.item;

import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class ZetaArmorItem extends ArmorItem implements IZetaItemExtensions {
	public ZetaArmorItem(ArmorMaterial mat, Type type, Properties props) {
		super(mat, type, props);
	}
}
