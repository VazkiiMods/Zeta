package org.violetmoon.zeta.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.IDisableable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

public abstract class CreativeTabHandler {

    private static final Map<ItemLike, Item> itemLikeCache = new HashMap<>();

    protected final Map<ResourceKey<CreativeModeTab>, CreativeTabAdditions> additions = new HashMap<>();
    protected final Multimap<ItemLike, ResourceKey<CreativeModeTab>> mappedItems = HashMultimap.create();

    private boolean daisyChainMode = false;
    private ItemSet daisyChainedSet = null;

    public void daisyChain() {
        daisyChainMode = true;
        daisyChainedSet = null;
    }

    public void endDaisyChain() {
        daisyChainMode = false;
        daisyChainedSet = null;
    }

    public void addToCreativeTab(ResourceKey<CreativeModeTab> tab, ItemLike item) {
        if (daisyChainMode) {
            if (daisyChainedSet == null)
                throw new IllegalArgumentException("Must start daisy chain with addToCreativeTabNextTo");

            addToDaisyChain(item);
        } else
            getForTab(tab).appendToEnd.add(item);

        mappedItems.put(item, tab);
    }

    public void addToCreativeTabNextTo(ResourceKey<CreativeModeTab> tab, ItemLike item, ItemLike target, boolean behind) {
        tab = guessTab(target, tab);
        CreativeTabAdditions additions = getForTab(tab);
        Map<ItemSet, ItemLike> map = (behind ? additions.appendBehind : additions.appendInFront);

        ItemSet toAdd = null;

        if (daisyChainMode) {
            boolean newSet = daisyChainedSet == null;
            ItemSet set = addToDaisyChain(item);

            if (newSet)
                toAdd = set;
        } else
            toAdd = new ItemSet(item);

        if (toAdd != null)
            map.put(toAdd, target);

        mappedItems.put(item, tab);
    }

    private ItemSet addToDaisyChain(ItemLike item) {
        if (daisyChainMode && daisyChainedSet != null) {
            daisyChainedSet.items.add(item);
            return daisyChainedSet;
        }

        ItemSet set = new ItemSet(item);
        if (daisyChainMode)
            daisyChainedSet = set;

        return set;
    }

    private ResourceKey<CreativeModeTab> guessTab(ItemLike parent, ResourceKey<CreativeModeTab> tab) {
        if (parent != null && mappedItems.containsKey(parent))
            tab = mappedItems.get(parent).iterator().next();

        return tab;
    }

    protected CreativeTabAdditions getForTab(ResourceKey<CreativeModeTab> tab) {
        return additions.computeIfAbsent(tab, tabRk -> new CreativeTabAdditions());
    }

    protected boolean isItemEnabled(ItemLike item) {
        if (item instanceof IDisableable<?> id)
            return id.isEnabled();
        return true;
    }

    protected void addToEntries(ItemStack target, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, ItemLike item, boolean behind) {
        logVerbose(() -> "adding target=" + target + " next to " + item + " with behind=" + behind);
        if (!isItemEnabled(item))
            return;

        List<ItemStack> stacksToAdd = Arrays.asList(new ItemStack(item));
        if (item instanceof AppendsUniquely au)
            stacksToAdd = au.appendItemsToCreativeTab();

        if (!behind)
            Collections.reverse(stacksToAdd);

        for (ItemStack addStack : stacksToAdd) {
            if (behind)
                entries.putBefore(target, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
            else
                entries.putAfter(target, addStack, TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    /**
     * Returns true if the item needs to be tried again later
     */
    protected boolean appendNextTo(ResourceKey<CreativeModeTab> tabKey, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, Map<ItemSet, ItemLike> map, boolean behind, boolean log) {
        logVerbose(() -> "appendNextTo " + tabKey + " / behind=" + behind);
        Collection<ItemSet> coll = map.keySet();
        if (coll.isEmpty())
            throw new RuntimeException("Tab collection is empty, this should never happen.");

        ItemSet firstSet = coll.iterator().next();
        ItemLike firstSetItem = firstSet.items.get(0);
        ItemLike target = map.get(firstSet);
        logVerbose(() -> "target is " + target);

        if (log) {
            ZetaMod.LOGGER.error("Creative tab loop found when adding {} next to {}", firstSetItem, target);
            ZetaMod.LOGGER.error("For more info enable Creative Verbose Logging in the Zeta config, or set Force Creative Tab Appends to true to disable this behavior");
        }

        map.remove(firstSet);

        if (!isItemEnabled(firstSetItem) || target == null) {
            logVerbose(() -> "hit early false return");
            return false;
        }

        if (!itemLikeCache.containsKey(target))
            itemLikeCache.put(target, target.asItem());
        Item targetItem = itemLikeCache.get(target);

        for (Entry<ItemStack, TabVisibility> entry : entries) {
            ItemStack stack = entry.getKey();
            Item item = stack.getItem();

            logVerbose(() -> "Comparing item " + item + " to our target " + targetItem);

            if (item == targetItem) {
                logVerbose(() -> "Matched! Adding successfully");
                for (int i = 0; i < firstSet.items.size(); i++) {
                    int j = i;
                    if (!behind)
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

    protected void logVerbose(Supplier<String> s) {
        if (ZetaGeneralConfig.enableCreativeVerboseLogging)
            ZetaMod.LOGGER.warn(s.get());
    }

    protected static class CreativeTabAdditions {

        public final List<ItemLike> appendToEnd = new ArrayList<>();
        public final Map<ItemSet, ItemLike> appendInFront = new LinkedHashMap<>();
        public final Map<ItemSet, ItemLike> appendBehind = new LinkedHashMap<>();

    }

    protected static class ItemSet {

        public List<ItemLike> items = new ArrayList<>();

        public ItemSet(ItemLike item) {
            items.add(item);
        }

    }

    public interface AppendsUniquely extends ItemLike {
        List<ItemStack> appendItemsToCreativeTab();
    }
}
