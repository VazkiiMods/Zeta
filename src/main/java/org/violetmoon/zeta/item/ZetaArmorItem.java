package org.violetmoon.zeta.item;

import net.minecraft.core.Holder;
import org.checkerframework.checker.units.qual.A;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class ZetaArmorItem extends ArmorItem implements IZetaItemExtensions {
	public ZetaArmorItem(Holder<ArmorMaterial> mat, Type type, Properties props) {
		super(mat, type, props);
	}
}
