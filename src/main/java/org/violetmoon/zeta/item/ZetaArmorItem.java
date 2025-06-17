package org.violetmoon.zeta.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.equipment.ArmorMaterial;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

public class ZetaArmorItem extends ArmorItem implements IZetaItemExtensions {
	public ZetaArmorItem(Holder<ArmorMaterial> mat, Type type, Properties props) {
		super(mat, type, props);
	}
}
