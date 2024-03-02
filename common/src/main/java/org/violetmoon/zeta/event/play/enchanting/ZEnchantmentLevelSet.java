/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package org.violetmoon.zeta.event.play.enchanting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

/**
 * Fired when the enchantment level is set for each of the three potential enchantments in the enchanting table.
 * The {@link #getLevel()} is set to the vanilla value and can be modified by this event handler.
 * <p>
 * The {@link #getEnchantRow()} is used to determine which enchantment level is being set, 1, 2, or 3. The {@link #getPower()} is a number
 * from 0-15 and indicates how many bookshelves surround the enchanting table. The {@link #getItem()} representing the item being
 * enchanted is also available.
 */
public interface ZEnchantmentLevelSet extends IZetaPlayEvent {
    /**
     * Get the world object
     *
     * @return the world object
     */
    Level getLevel();

    /**
     * Get the pos of the enchantment table
     *
     * @return the pos of the enchantment table
     */
    BlockPos getPos();

    /**
     * Get the row for which the enchantment level is being set
     *
     * @return the row for which the enchantment level is being set
     */
    int getEnchantRow();

    /**
     * Get the power (# of bookshelves) for the enchanting table
     *
     * @return the power (# of bookshelves) for the enchanting table
     */
    int getPower();

    /**
     * Get the item being enchanted
     *
     * @return the item being enchanted
     */
    @NotNull ItemStack getItem();

    /**
     * Get the original level of the enchantment for this row (0-30)
     *
     * @return the original level of the enchantment for this row (0-30)
     */
    int getOriginalLevel();

    /**
     * Get the level of the enchantment for this row (0-30)
     *
     * @return the level of the enchantment for this row (0-30)
     */
    int getEnchantLevel();

    /**
     * Set the new level of the enchantment (0-30)
     *
     * @param level the new level of the enchantment (0-30)
     */
    void setEnchantLevel(int level);
}
