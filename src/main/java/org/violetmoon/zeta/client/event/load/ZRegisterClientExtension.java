package org.violetmoon.zeta.client.event.load;

import net.minecraft.world.item.Item;
import org.violetmoon.zeta.client.extensions.IZetaClientItemExtensions;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZRegisterClientExtension extends IZetaLoadEvent {
    /**
     * Used for registering extensions under multiple items at once.
     * @param extension A set of clientside item extensions being used
     * @param items A collection of items
     */
    default void registerItems(IZetaClientItemExtensions extension, Item... items) {
        for (Item item : items) {
            registerItem(extension, item);
        }
    }

    /**
     * Registers extensions for the item
     * @param extension A set of clientside item extensions being used
     * @param item A given item
     */
    void registerItem(IZetaClientItemExtensions extension, Item item);
}
