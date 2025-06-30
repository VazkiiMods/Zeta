package org.violetmoon.zeta.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.IDisableable;

import java.util.*;
import java.util.function.Supplier;

public class CreativeTabManager {

	private static final Object MUTEX = new Object();

	private static final Map<ResourceKey<CreativeModeTab>, CreativeTabAdditions> additions = new HashMap<>();
	private static final Multimap<ItemLike, ResourceKey<CreativeModeTab>> mappedItems = HashMultimap.create();

	private static boolean daisyChainMode = false;
	private static ItemSet daisyChainedSet = null;
	
	public static void daisyChain() {
		daisyChainMode = true;
		daisyChainedSet = null;
	}
	
	public static void endDaisyChain() {
		daisyChainMode = false;
		daisyChainedSet = null;
	}
	
	public static void addToCreativeTab(ResourceKey<CreativeModeTab> tab, ItemLike item) {
		if (daisyChainMode) {
			if (daisyChainedSet == null) throw new IllegalArgumentException("Must start daisy chain with addToCreativeTabNextTo");
			addToDaisyChain(item);
		} else {
			getForTab(tab).appendToEnd.add(item);
		}
		mappedItems.put(item, tab);
	}

	public static void addToCreativeTabNextTo(ResourceKey<CreativeModeTab> tab, ItemLike item, ItemLike target, boolean behind) {
		tab = guessTab(target, tab);
		CreativeTabAdditions additions = getForTab(tab);
		Map<ItemSet, ItemLike> map = (behind ? additions.appendBehind : additions.appendInFront);
		ItemSet toAdd = null;
		
		if(daisyChainMode) {
			boolean newSet = daisyChainedSet == null;
			ItemSet set = addToDaisyChain(item);

			if(newSet)
				toAdd = set;
		} else {
			toAdd = new ItemSet(item);
		}

		if(toAdd != null)
			map.put(toAdd, target);

		mappedItems.put(item, tab);
	}
	
	private static ItemSet addToDaisyChain(ItemLike item) {
		if (daisyChainMode && daisyChainedSet != null) {
			daisyChainedSet.items.add(item);
			return daisyChainedSet;
		}
		
		ItemSet set = new ItemSet(item);
		if(daisyChainMode)
			daisyChainedSet = set;
		
		return set;
	}
	
	private static ResourceKey<CreativeModeTab> guessTab(ItemLike parent, ResourceKey<CreativeModeTab> tab) {
		return (parent != null && mappedItems.containsKey(parent)) ? mappedItems.get(parent).iterator().next() : tab;
    }
	
	private static CreativeTabAdditions getForTab(ResourceKey<CreativeModeTab> tab) {
		return additions.computeIfAbsent(tab, tabRk -> new CreativeTabAdditions());
	}

	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		synchronized(MUTEX) {

			ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
			
			if(additions.containsKey(tabKey)) {
				CreativeTabAdditions add = additions.get(tabKey);

				for(ItemLike item : add.appendToEnd) {
					acceptItem(event, item);
				}

				if(ZetaGeneralConfig.forceCreativeTabAppends) {
					for(ItemSet itemset : add.appendInFront.keySet())
						for(ItemLike item : itemset.items)
							acceptItem(event, item);

					for(ItemSet itemset : add.appendBehind.keySet())
						for(ItemLike item : itemset.items)
							acceptItem(event, item);
					return;
				}

				Map<ItemSet, ItemLike> front = new LinkedHashMap<>(add.appendInFront);
				Map<ItemSet, ItemLike> behind = new LinkedHashMap<>(add.appendBehind);
				final int maxFails = 100;
				final int logThreshold = maxFails - 10;
				int failedAttempts = 0;

                while ((!front.isEmpty() && !behind.isEmpty()) || failedAttempts < 100) {
					if (!front.isEmpty()) {
						failedAttempts = addItems(event, front, false, failedAttempts > logThreshold) ? failedAttempts : failedAttempts + 1;
					}
					if (!behind.isEmpty()) {
						failedAttempts = addItems(event, behind, true, failedAttempts > logThreshold) ? failedAttempts : failedAttempts + 1;
					}
                }
			}
		}
	}

	private static boolean addItems(BuildCreativeModeTabContentsEvent event, Map<ItemSet, ItemLike> itemsMap, boolean insertAfter, boolean log) {
		Collection<ItemSet> collection = itemsMap.keySet();
		ItemSet itemsToAdd = collection.iterator().next();
		ItemLike firstSetItem = itemsToAdd.items.getFirst();
		ItemLike target = itemsMap.get(itemsToAdd);
		logVerbose(() -> "target is " + target);

		itemsMap.remove(itemsToAdd);

		if(log) {
			ZetaMod.LOGGER.error("Creative tab loop found when adding {} next to {}", firstSetItem, target);
			ZetaMod.LOGGER.error("For more info enable Creative Verbose Logging in the Zeta config, or set Force Creative Tab Appends to true to disable this behavior");
		}

		if (!isItemEnabled(firstSetItem) || target == null) return true;

		ItemStack targetStack = new ItemStack(target);

		for (ItemLike item : itemsToAdd.items) {
			if (!isItemEnabled(item)) continue;
			List<ItemStack> stacksToAdd = List.of(new ItemStack(item));

			if (item instanceof AppendsUniquely au) {
				stacksToAdd = au.appendItemsToCreativeTab();
			}

			if(!insertAfter) {
				Collections.reverse(stacksToAdd);
			}

			for(ItemStack addStack : stacksToAdd) {
				if (insertAfter) {
					try {
						event.insertAfter(targetStack, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
					} catch (IllegalArgumentException exception) {
						logVerbose(exception::getMessage);
						itemsMap.put(itemsToAdd, target);
						return false;
					}
				} else {
					try {
						event.insertBefore(targetStack, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
					} catch (IllegalArgumentException exception) {
						logVerbose(exception::getMessage);
						itemsMap.put(itemsToAdd, target);
						return false;
					}
				}
			}
		}
		return true;
	}

	private static boolean isItemEnabled(ItemLike item) {
		return !(item instanceof IDisableable<?> id) || id.isEnabled();
	}

	private static void acceptItem(BuildCreativeModeTabContentsEvent event, ItemLike item) {
		if (!isItemEnabled(item)) return;

		if(item instanceof AppendsUniquely au)
			event.acceptAll(au.appendItemsToCreativeTab());
		else
			event.accept(item);
	}

	private static void logVerbose(Supplier<String> s) {
		if(ZetaGeneralConfig.enableCreativeVerboseLogging)
			ZetaMod.LOGGER.warn(s.get());
	}

	private static class CreativeTabAdditions {

		private final List<ItemLike> appendToEnd = new ArrayList<>();
		private final Map<ItemSet, ItemLike> appendInFront = new LinkedHashMap<>();
		private final Map<ItemSet, ItemLike> appendBehind = new LinkedHashMap<>();
	}
	
	private static class ItemSet {
		
		List<ItemLike> items = new ArrayList<>();
	
		public ItemSet(ItemLike item) {
			items.add(item);
		}
	}

	public interface AppendsUniquely extends ItemLike {
		List<ItemStack> appendItemsToCreativeTab();
	}
}