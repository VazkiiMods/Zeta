package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgeZAddItemColorHandlers implements ZAddItemColorHandlers {
	protected final RegisterColorHandlersEvent.Item e;

	public ForgeZAddItemColorHandlers(RegisterColorHandlersEvent.Item e) {
		this.e = e;
	}

	@Override
	public void register(ItemColor c, ItemLike... items) {
		e.register(c, items);
	}

	@Override
	public void registerNamed(Zeta myZeta, Function<Item, ItemColor> c, String... names) {
		for (String name : names) {
			myZeta.registry.assignItemColor(name, b -> register(c.apply(b), b));
		}
	}

	@Override
	public ItemColors getItemColors() {
		return e.getItemColors();
	}

}
