package org.violetmoon.zeta.client.event.load;

import java.util.Map;
import java.util.function.Function;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface ZAddItemColorHandlers extends IZetaLoadEvent {
	void register(ItemColor c, ItemLike... items);
	void registerNamed(Zeta myZeta, Function<Item, ItemColor> c, String... names);
	ItemColors getItemColors();
}
