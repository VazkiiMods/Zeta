package org.violetmoon.zeta.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

//JANKY SHIT FOR MIXIN TARGETS
public class ZetaBlockItem extends BlockItem implements IZetaItemExtensions {
	public ZetaBlockItem(Block toPlace, Properties props) {
		super(toPlace, props);

		//TODO maybe some stuff about constructor registration or whatever
		// ZetaBlockItem is used when registering "regular" blocks as well, so that's something
		// to watch out for wrt constructor registration
	}
}
