package org.violetmoon.zeta.item;

import org.violetmoon.zeta.module.IDisableable;
import org.violetmoon.zeta.registry.CreativeTabManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface IZetaItem extends IDisableable<IZetaItem> {

	default Item getItem() {
		return (Item) this;
	}

	default Item setCreativeTab(ResourceKey<CreativeModeTab> tab) {
		Item item = getItem();
		CreativeTabManager.addToTab(tab, item);
		return item;
	}
	
    default Item setCreativeTab(ResourceKey<CreativeModeTab> tab, ItemLike parent, boolean behindParent) {
    	Item item = getItem();
    	CreativeTabManager.addNextToItem(tab, item, parent, behindParent);
    	
    	return item;
    }

}
