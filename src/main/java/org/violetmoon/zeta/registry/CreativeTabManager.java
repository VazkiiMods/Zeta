package org.violetmoon.zeta.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.IDisableable;

import javax.annotation.Nullable;
import java.util.*;

public class CreativeTabManager {
    private static final Object MUTEX = new Object();
    private static final Map<ResourceKey<CreativeModeTab>, CreativeTabAdditions> additions = new HashMap<>();

    private static DaisyChain chain = null;
    private static final boolean ignChaining = false;


    public static void startChain(ResourceKey<CreativeModeTab> tab, boolean reversed, boolean behindParent, @Nullable ItemLike appendParent) {
        if (chain != null) {
            ZetaMod.LOGGER.error("CHAIN WAS STARTED WITHOUT FINISHING IT! DO NOT DO THIS!!! Ending chain now.");
            endChain();
        }

        chain = new DaisyChain(tab, reversed, behindParent, appendParent);
    }

    public static DaisyChain queryChain() {
        return chain;
    }

    public static void endChain() {
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

                //I hate this I hate this I hate this I hate this I hate this I hate this I hate this I hate this I hate this
                Map<ItemLike, Map<Boolean, List<ItemLike>>> additionsAtAllItems = new HashMap<>();

                // Merge the items together. Needs to be done in a way where entries can have either in front or behind
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
        private final Map<ItemLike, List<ItemLike>> appendBehind = new LinkedHashMap<>();
        private final Map<ItemLike, List<ItemLike>> appendInFront = new LinkedHashMap<>();
        private List<ItemLike> addedItems = new ArrayList<>();

        private void addBehind(ItemLike item, ItemLike parent, boolean addFirst) {
            if (!validateItem(item)) return;

            if (!appendBehind.containsKey(parent)) {
                appendBehind.put(parent, new LinkedList<>());
            }

            if (!addFirst) appendBehind.get(parent).add(item);
            else appendBehind.get(parent).addFirst(item);
        }

        private void addInFront(ItemLike item, ItemLike parent, boolean addFirst) {
            if (!validateItem(item)) return;

            if (!appendInFront.containsKey(parent)) {
                appendInFront.put(parent, new LinkedList<>());
            }

            if (!addFirst) appendInFront.get(parent).add(item);
            else appendInFront.get(parent).addFirst(item);
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
