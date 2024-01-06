package org.violetmoon.zeta.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.ZItemTooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;

//TODO: janky
public class RequiredModTooltipHandler {

	private final Map<Item, String> items = new HashMap<>();
	private final Map<Block, String> blocks = new HashMap<>(); //TODO: only needed because it's called in constructors where Block.asItem isn't set up yet

	public void map(Item item, String mod) {
		items.put(item, mod);
	}

	public void map(Block block, String mod) {
		blocks.put(block, mod);
	}

	public List<ItemStack> disabledItems() {
		if(!GeneralConfig.hideDisabledContent)
			return new ArrayList<>();
		
		return items.entrySet().stream()
				.filter((entry) -> !ModList.get().isLoaded(entry.getValue()))
				.map((entry) -> new ItemStack(entry.getKey()))
				.toList();
	}

	public static class Client {
		private final Zeta z;

		public Client(Zeta z) {
			this.z = z;
		}

		@PlayEvent
		public void onTooltip(ZItemTooltip event) {
			Map<Item, String> ITEMS = z.requiredModTooltipHandler.items;
			Map<Block, String> BLOCKS = z.requiredModTooltipHandler.blocks;

			if(!BLOCKS.isEmpty() && event.getEntity() != null && event.getEntity().level() != null) {
				for(Block b : BLOCKS.keySet())
					ITEMS.put(b.asItem(), BLOCKS.get(b));
				BLOCKS.clear();
			}

			Item item = event.getItemStack().getItem();
			if(ITEMS.containsKey(item)) {
				String mod = ITEMS.get(item);
				if (!z.isModLoaded(mod)) {
					event.getToolTip().add(Component.translatable("quark.misc.mod_disabled", mod).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
