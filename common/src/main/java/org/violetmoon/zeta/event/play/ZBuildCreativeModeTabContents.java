package org.violetmoon.zeta.event.play;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.forgeapi.common.util.MutableHashedLinkedMap;

import java.util.Collection;
import java.util.function.Supplier;

// TODO abstract this with BuildCreativeModeTabContentsEvent on forge and fabric's creative tab event callback
public interface ZBuildCreativeModeTabContents extends IZetaPlayEvent {
    /**
     * {@return the creative mode tab currently populating its contents}
     */
    CreativeModeTab getTab();

    /**
     * {@return the key of the creative mode tab currently populating its contents}
     */
    ResourceKey<CreativeModeTab> getTabKey();

    FeatureFlagSet getFlags();

    CreativeModeTab.ItemDisplayParameters getParameters();

    boolean hasPermissions();

    MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> getEntries();

    void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility);
    void accept(Supplier<? extends ItemLike> item, CreativeModeTab.TabVisibility visibility);
    void accept(Supplier<? extends ItemLike> item);

    default void accept(ItemStack itemStack) {
        this.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    default void accept(ItemLike itemLike, CreativeModeTab.TabVisibility pTabVisibility) {
        this.accept(new ItemStack(itemLike), pTabVisibility);
    }

    default void accept(ItemLike itemLike) {
        this.accept(new ItemStack(itemLike), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    default void acceptAll(Collection<ItemStack> stacks, CreativeModeTab.TabVisibility pTabVisibility) {
        stacks.forEach((itemStack) -> {
            this.accept(itemStack, pTabVisibility);
        });
    }

    default void acceptAll(Collection<ItemStack> stacks) {
        this.acceptAll(stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
