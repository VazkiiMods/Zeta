package org.violetmoon.zetaimplforge.client.event.load;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.violetmoon.zeta.registry.ZetaRegistry;

public class ForgeZAddItemColorHandlers implements ZAddItemColorHandlers {
	protected final RegisterColorHandlersEvent.Item e;
	protected final Map<String, Function<Item, ItemColor>> namedItemColors = new HashMap<>();
	private final ZetaRegistry zetaRegistry;

	public ForgeZAddItemColorHandlers(RegisterColorHandlersEvent.Item e, ZetaRegistry zetaRegistry) {
		this.e = e;
		this.zetaRegistry = zetaRegistry;
	}

	@Override
	public void register(ItemColor c, ItemLike... items) {
		e.register(c, items);
	}

	@Override
	public void registerNamed(Function<Item, ItemColor> c, String... names) {
		for (String name : names) {
			zetaRegistry.assignItemColor(name, b -> register(c.apply(b), b));
		}
	}

	@Override
	public ItemColors getItemColors() {
		return e.getItemColors();
	}

}
