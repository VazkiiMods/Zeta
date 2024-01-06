package org.violetmoon.zeta.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

public class ZetaArmorItem extends ArmorItem implements IZetaItemExtensions {
	public ZetaArmorItem(ArmorMaterial mat, Type type, Properties props) {
		super(mat, type, props);
	}
}
