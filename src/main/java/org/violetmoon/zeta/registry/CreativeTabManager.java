package org.violetmoon.zeta.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.IDisableable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CreativeTabManager {
    private static final Object MUTEX = new Object();
    private static final Map<ResourceKey<CreativeModeTab>, CreativeTabAdditions> additions = new HashMap<>();

    private static DaisyChain chain = null;
    private static final boolean ignChaining = false;


    public synchronized static void startChain(ResourceKey<CreativeModeTab> tab, boolean reversed, boolean behindParent, @Nullable ItemLike appendParent) {
        if (chain != null) {
            ZetaMod.LOGGER.error("CHAIN WAS STARTED WITHOUT FINISHING IT! DO NOT DO THIS!!! Ending chain now.");
            endChain();
        }

        chain = new DaisyChain(tab, reversed, behindParent, appendParent);
    }

    public static DaisyChain queryChain() {
        return chain;
    }

    public synchronized static void endChain() {
        for (ItemLike itemFromChain : chain.chainedItems) {
            CreativeTabAdditions tabAdditions = getForTab(chain.getTab());
            if (chain.appendParent == null) {
                tabAdditions.addAtEnd(itemFromChain);
            } else if (!chain.behindParent) {
                tabAdditions.addInFront(itemFromChain, chain.appendParent, false);
            } else {
                tabAdditions.addBehind(itemFromChain, chain.getParent(), false);
            }
        }

        chain = null;
    }


    public static void addToTab(ResourceKey<CreativeModeTab> tab, ItemLike item) {
        if (chain != null && chain.getTab().equals(tab) && !ignChaining) {
            chain.addToChain(item);
        } else {
            getForTab(tab).addAtEnd(item);
        }
    }

    public static void addNextToItem(ResourceKey<CreativeModeTab> tab, ItemLike item, ItemLike target, boolean behind) {
        if (chain != null && chain.getTab().equals(tab) && !ignChaining) {
            chain.addToChain(item);
        } else {
            CreativeTabAdditions tabAdditions = getForTab(tab);

            if (behind) {
                tabAdditions.addBehind(item, target, false);
            } else {
                tabAdditions.addInFront(item, target, false);
            }
        }
    }

    private static CreativeTabAdditions getForTab(ResourceKey<CreativeModeTab> tab) {
        return additions.computeIfAbsent(tab, tabRk -> new CreativeTabAdditions());
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        synchronized(MUTEX) {
            ResourceKey<CreativeModeTab> tabKey = event.getTabKey();

            if (additions.containsKey(tabKey)) {
                CreativeTabAdditions tabAdditions = additions.get(tabKey);

                for (ItemLike item : tabAdditions.appendToEnd) {
                    acceptItem(event, item);
                }

                /*for (Map.Entry<ItemLike, List<ItemLike>> entry : tabAdditions.appendInFront.entrySet()) {
                    ItemLike parent = entry.getKey();
                    for (ItemLike item : entry.getValue()) {
                        acceptItemAtParent(event, item, parent, false);
                    }
                }

                for (Map.Entry<ItemLike, List<ItemLike>> entry : tabAdditions.appendBehind.entrySet()) {
                    ItemLike parent = entry.getKey();
                    for (ItemLike item : entry.getValue()) {
                        acceptItemAtParent(event, item, parent, true);
                    }
                }*/

                AtomicInteger firstTryIdiots = new AtomicInteger(0);
                AtomicInteger inOnSecond = new AtomicInteger(0);
                AtomicInteger failedBoth = new AtomicInteger(0);
                AtomicInteger doubleJeopardy = new AtomicInteger(0);
                AtomicInteger dailyDouble = new AtomicInteger(0);

                Multimap<ItemLike, Pair<Boolean, ItemLike>> additionsAtAllItems = LinkedHashMultimap.create();

                tabAdditions.appendInFront.forEach((parent, child) -> {
                    additionsAtAllItems.put(parent, new Pair<>(false, child));
                });
                tabAdditions.appendBehind.forEach((parent, child) -> {
                    additionsAtAllItems.put(parent, new Pair<>(true, child));
                });

                Multimap<ItemLike, Pair<Boolean, ItemLike>> round2 = LinkedHashMultimap.create();

                additionsAtAllItems.forEach((parent, childPair) -> {
                    if (!isItemEnabled(parent)) {
                        acceptItem(event, childPair.getSecond());
                    } else if (!event.getParentEntries().contains(parent.asItem().getDefaultInstance())) {
                        round2.put(parent, childPair);
                    } else if (event.getParentEntries().contains(childPair.getSecond().asItem().getDefaultInstance())) {
                        ZetaMod.LOGGER.debug("DOUBLE JEOPARDY FOR " + childPair.getSecond().asItem().getDefaultInstance().getDisplayName().getString());
                        doubleJeopardy.getAndIncrement();
                    } else {
                        acceptItemAtParent(event, childPair.getSecond(), parent, childPair.getFirst());
                        firstTryIdiots.getAndIncrement();
                    }
                });

                round2.forEach((parent, childPair) -> {
                    if (event.getParentEntries().contains(childPair.getSecond().asItem().getDefaultInstance())) {
                        ZetaMod.LOGGER.debug("HOLY SHIT DAILY DOUBLE?!?! FOR " + childPair.getSecond().asItem().getDefaultInstance().getDisplayName().getString());
                        dailyDouble.getAndIncrement();
                    } else if (event.getParentEntries().contains(parent.asItem().getDefaultInstance())) {
                        acceptItemAtParent(event, childPair.getSecond(), parent, childPair.getFirst());
                        inOnSecond.getAndIncrement();
                    } else {
                        acceptItem(event, childPair.getSecond());
                        failedBoth.getAndIncrement();
                    }
                });

                ZetaMod.LOGGER.debug(tabKey.location() + " - " + firstTryIdiots + "/" + inOnSecond + "/" + failedBoth + " - " + doubleJeopardy + "/" + dailyDouble);
            }
        }
    }

    private static boolean isItemEnabled(ItemLike item) {
        return !(item instanceof IDisableable<?> id) || id.isEnabled();
    }

    private static void acceptItem(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        if (!isItemEnabled(item)) return;

        if(item instanceof CreativeTabManager.AppendsUniquely au)
            event.acceptAll(au.appendItemsToCreativeTab());
        else
            event.accept(item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void acceptItemAtParent(BuildCreativeModeTabContentsEvent event, ItemLike item, ItemLike parent, boolean behind) {
        if (!isItemEnabled(item)) return;

        ItemStack parentStack = parent.asItem().getDefaultInstance();

        if (item instanceof AppendsUniquely au)
            for (ItemStack uniques : au.appendItemsToCreativeTab()) {
                if (behind) {
                    event.insertBefore(parentStack, uniques, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                } else {
                    event.insertAfter(parentStack, uniques, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        else {
            ItemStack itemStack = item.asItem().getDefaultInstance();
            if (behind) {
                event.insertBefore(parentStack, itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            } else {
                event.insertAfter(parentStack, itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    /**
     * Note: This will only return one tab. If Item is in multiple, it is what it is. <br>
     * Defaults to Building Blocks if one could not be found <br>
     * This might not contain all items in it either. Have fun ;)
     * @return The tab.
     */
    public static ResourceKey<CreativeModeTab> getTabOfItem(ItemLike itemLike) {
        for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeTabAdditions> entry : additions.entrySet()) {
            if (entry.getValue().addedItems.contains(itemLike)) {
                return entry.getKey();
            }
        }
        return CreativeModeTabs.BUILDING_BLOCKS;
    }

    public static class DaisyChain {
        private final ResourceKey<CreativeModeTab> tab;
        private final boolean reversed;
        private final boolean behindParent;
        private final @Nullable ItemLike appendParent;

        private List<ItemLike> chainedItems = new ArrayList<>();

        public DaisyChain(ResourceKey<CreativeModeTab> tab, boolean reversed, boolean behindParent, @Nullable ItemLike appendParent) {
            this.tab = tab;
            this.behindParent = behindParent;
            this.reversed = reversed;
            this.appendParent = appendParent;
        }

        // Use this one if you just need to append at end.
        public DaisyChain(ResourceKey<CreativeModeTab> tab, boolean reversed) {
            this(tab, reversed, reversed, null);
        }

        public ResourceKey<CreativeModeTab> getTab() {
            return this.tab;
        }

        public @Nullable ItemLike getParent() {
            return appendParent;
        }

        public void addToChain(ItemLike item) {
            if ((!reversed)) {
                chainedItems.add(item);
            } else {
                chainedItems.addFirst(item);
            }
        }
    }

    private static class CreativeTabAdditions {
        private final List<ItemLike> appendToEnd = new ArrayList<>();
        private final Multimap<ItemLike, ItemLike> appendBehind = LinkedHashMultimap.create();
        private final Multimap<ItemLike, ItemLike> appendInFront = LinkedHashMultimap.create();
        private List<ItemLike> addedItems = new ArrayList<>();

        private void addBehind(ItemLike item, ItemLike parent, boolean addFirst) {
            if (!validateItem(item)) return;

            appendBehind.put(parent, item);
        }

        private void addInFront(ItemLike item, ItemLike parent, boolean addFirst) {
            if (!validateItem(item)) return;

            appendInFront.put(parent, item);
        }

        private void addAtEnd(ItemLike item) {
            if (!validateItem(item)) return;

            appendToEnd.add(item);
        }

        public boolean validateItem(ItemLike item) {
            if (addedItems.contains(item)) {
                ZetaMod.LOGGER.debug("DUPLICATED ITEM IN TAB - " + item.asItem().getDefaultInstance().getDisplayName().tryCollapseToString());
                return false;
            }

            addedItems.add(item);
            return true;
        }
    }

    public interface AppendsUniquely extends ItemLike {
        List<ItemStack> appendItemsToCreativeTab();
    }
}
