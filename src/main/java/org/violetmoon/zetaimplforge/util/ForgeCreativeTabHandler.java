package org.violetmoon.zetaimplforge.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.registry.CreativeTabHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForgeCreativeTabHandler extends CreativeTabHandler {

    @SubscribeEvent
    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        synchronized (this) {
            ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
            MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

            if (additions.containsKey(tabKey)) {
                CreativeTabAdditions add = additions.get(tabKey);

                for (ItemLike item : add.appendToEnd)
                    acceptItem(event, item);

                if (ZetaGeneralConfig.forceCreativeTabAppends) {
                    for (ItemSet itemset : add.appendInFront.keySet())
                        for (ItemLike item : itemset.items)
                            acceptItem(event, item);
                    for (ItemSet itemset : add.appendBehind.keySet())
                        for (ItemLike item : itemset.items)
                            acceptItem(event, item);

                    return;
                }

                Map<ItemSet, ItemLike> front = new LinkedHashMap<>(add.appendInFront);
                Map<ItemSet, ItemLike> behind = new LinkedHashMap<>(add.appendBehind);

                final int failsafe = 100;
                final int printThreshold = failsafe - 10;

                int misses = 0;
                boolean failsafing = false;

                while (true) {
                    boolean missed = false;
                    logVerbose(() -> "front empty=" + front.isEmpty() + " / behind empty=" + behind.isEmpty());

                    if (entries.isEmpty()) {
                        ZetaMod.LOGGER.error("entries map for tab {} is empty, this should never happen", tabKey);
                        return;
                    }

                    if (!front.isEmpty())
                        missed = appendNextTo(tabKey, entries, front, false, failsafing);
                    if (!behind.isEmpty())
                        missed |= appendNextTo(tabKey, entries, behind, true, failsafing);

                    if (missed) {
                        int fMisses = misses;
                        logVerbose(() -> "Missed " + fMisses + "times out of " + failsafe);

                        misses++;
                    }

                    // arbitrary failsafe, should never happen
                    if (misses > failsafe) {
                        logVerbose(() -> {
                            StringBuilder sb = new StringBuilder();
                            for (Map.Entry<ItemStack, CreativeModeTab.TabVisibility> entry : entries) {
                                sb.append(entry.getKey());
                                sb.append("; ");
                            }
                            return sb.toString();
                        });
                        new RuntimeException("Creative tab placement misses exceeded failsafe, aborting logic").printStackTrace();
                        return;
                    }
                    if (misses > printThreshold)
                        failsafing = true;

                    if (front.isEmpty() && behind.isEmpty())
                        return;
                }
            }
        }
    }

    private void acceptItem(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        if (!isItemEnabled(item))
            return;

        if (item instanceof AppendsUniquely au)
            event.acceptAll(au.appendItemsToCreativeTab());
        else
            event.accept(item);
    }

}
