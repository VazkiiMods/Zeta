package org.violetmoon.zeta.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.module.IDisableable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class CreativeTabManager {

	private static final Object MUTEX = new Object();
	private static final Map<ItemLike, Item> itemLikeCache = new HashMap<>();

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
		if(daisyChainMode) {
			if(daisyChainedSet == null)
				throw new IllegalArgumentException("Must start daisy chain with addToCreativeTabNextTo");
			
			addToDaisyChain(item);
		} 
		else 
			getForTab(tab).appendToEnd.add(item);
		
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
		} 
		else
			toAdd = new ItemSet(item);
		
		if(toAdd != null)
			map.put(toAdd, target);
		
		mappedItems.put(item, tab);
	}
	
	private static ItemSet addToDaisyChain(ItemLike item) {
		if(daisyChainMode && daisyChainedSet != null) {
			daisyChainedSet.items.add(item);
			return daisyChainedSet;
		}
		
		ItemSet set = new ItemSet(item);
		if(daisyChainMode)
			daisyChainedSet = set;
		
		return set;
	}
	
	private static ResourceKey<CreativeModeTab> guessTab(ItemLike parent, ResourceKey<CreativeModeTab> tab) {
		if(parent != null && mappedItems.containsKey(parent))
			tab = mappedItems.get(parent).iterator().next();
		
		return tab;
	}
	
	private static CreativeTabAdditions getForTab(ResourceKey<CreativeModeTab> tab) {
		return additions.computeIfAbsent(tab, tabRk -> new CreativeTabAdditions());
	}

	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		synchronized(MUTEX) {
			ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
			MutableHashedLinkedMap<ItemStack, TabVisibility> entries = event.getEntries();
			
			if(additions.containsKey(tabKey)) {
				CreativeTabAdditions add = additions.get(tabKey);

				for(ItemLike item : add.appendToEnd)
					acceptItem(event, item);
				
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
				
				final int failsafe = 100;
				final int printThreshold = failsafe - 10;
				
				int misses = 0;
				boolean failsafing = false;
				
            	while(true) {
            		boolean missed = false; 
            		logVerbose(() -> "front empty=" + front.isEmpty() + " / behind empty=" + behind.isEmpty());
            		
            		if(entries.isEmpty()) {
            			Zeta.GLOBAL_LOG.error("entries map for tab " + tabKey + " is empty, this should never happen");
            			return;
            		}
            		
            		if(!front.isEmpty())
            			missed = appendNextTo(tabKey, entries, front, false, failsafing);
            		if(!behind.isEmpty())
            			missed |= appendNextTo(tabKey, entries, behind, true, failsafing);

            		if(missed) {
            			int fMisses = misses;
            			logVerbose(() -> "Missed " + fMisses + "times out of " + failsafe);
            			
            			misses++;
            		}
            		
            		// arbitrary failsafe, should never happen
            		if(misses > failsafe) {
                  		logVerbose(() -> {
                			StringBuilder sb = new StringBuilder();
                			for(Entry<ItemStack, TabVisibility> entry : entries) {
                				sb.append(entry.getKey());
                				sb.append("; ");
                			}
                			return sb.toString();
                		});
            			new RuntimeException("Creative tab placement misses exceeded failsafe, aborting logic").printStackTrace();
            			return;
            		}
            		if(misses > printThreshold)
            			failsafing = true;
            		
            		if(front.isEmpty() && behind.isEmpty()) 
            			return;
            	}
			}
		}
	}

	private static boolean isItemEnabled(ItemLike item) {
		if(item instanceof IDisableable<?> id)
			return id.isEnabled();

		return true;
	}

	private static void acceptItem(BuildCreativeModeTabContentsEvent event, ItemLike item) {
		if(!isItemEnabled(item))
			return;
		
		if(item instanceof AppendsUniquely au)
			event.acceptAll(au.appendItemsToCreativeTab());
		else 
			event.accept(item);
	}
	
	private static void addToEntries(ItemStack target, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, ItemLike item, boolean behind) {
		logVerbose(() -> "adding target=" + target + " next to " + item + " with behind=" + behind);
		if(!isItemEnabled(item))
			return;
		
		List<ItemStack> stacksToAdd = Arrays.asList(new ItemStack(item));
		if(item instanceof AppendsUniquely au)
			stacksToAdd = au.appendItemsToCreativeTab();
		
		if(!behind)
			Collections.reverse(stacksToAdd);
		
		for(ItemStack addStack : stacksToAdd) {
			if(behind)
				entries.putBefore(target, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
			else 
				entries.putAfter(target, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}

	/**
	 * Returns true if the item needs to be tried again later 
	 */
	private static boolean appendNextTo(ResourceKey<CreativeModeTab> tabKey, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, Map<ItemSet, ItemLike> map, boolean behind, boolean log) {
		logVerbose(() -> "appendNextTo " + tabKey + " / behind=" + behind);
		Collection<ItemSet> coll = map.keySet();
		if(coll.isEmpty())
			throw new RuntimeException("Tab collection is empty, this should never happen.");

		ItemSet firstSet = coll.iterator().next();
		ItemLike firstSetItem = firstSet.items.get(0);
		ItemLike target = map.get(firstSet);
		logVerbose(() -> "target is " + target);
		
		if(log) {
			Zeta.GLOBAL_LOG.error("Creative tab loop found when adding {} next to {}", firstSetItem, target);
			Zeta.GLOBAL_LOG.error("For more info enable Creative Verbose Logging in the Zeta config, or set Force Creative Tab Appends to true to disable this behavior");
		}
		
		map.remove(firstSet);
		
		if(!isItemEnabled(firstSetItem) || target == null) {
			logVerbose(() -> "hit early false return");
			return false;
		}
		
		if(!itemLikeCache.containsKey(target))
			itemLikeCache.put(target, target.asItem());
		Item targetItem = itemLikeCache.get(target);
		
		for(Entry<ItemStack, TabVisibility> entry : entries) {
			ItemStack stack = entry.getKey();
			Item item = stack.getItem();
			
			logVerbose(() -> "Comparing item " + item + " to our target " + targetItem);
			
			if(item == targetItem) {
				logVerbose(() -> "Matched! Adding successfully");
				for(int i = 0; i < firstSet.items.size(); i++) {
					int j = i;
					if(!behind)
						j = firstSet.items.size() - 1 - i;
					
					addToEntries(stack, entries, firstSet.items.get(j), behind);
				}
				
				return false;
			}
		}
		
		// put the set back at the end of the map to try it again after the target is added 
		map.put(firstSet, target);
		return true;
	}
	
	private static void logVerbose(Supplier<String> s) {
		if(ZetaGeneralConfig.enableCreativeVerboseLogging)
			Zeta.GLOBAL_LOG.warn(s.get());
	}

	private static class CreativeTabAdditions {

		private List<ItemLike> appendToEnd = new ArrayList<>();
		private Map<ItemSet, ItemLike> appendInFront = new LinkedHashMap<>();
		private Map<ItemSet, ItemLike> appendBehind = new LinkedHashMap<>();
		
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
