/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package org.violetmoon.zeta.event.play.furnace;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.util.handler.FuelHandler;

/**
 * {@link ZFurnaceFuelBurnTime} is fired when determining the fuel value for an ItemStack. <br>
 * <br>
 * To set the burn time of your own item, use {@link FuelHandler#addFuel} instead.<br>
 * <br>
 * This event is {@link org.violetmoon.zeta.event.bus.Cancellable} to prevent later handlers from changing the value.<br>
 **/
public interface ZFurnaceFuelBurnTime extends IZetaPlayEvent {
    /**
     * Get the ItemStack "fuel" in question.
     */
    @NotNull ItemStack getItemStack();

    /**
     *
     * Get the recipe type for which to obtain the burn time, if known.
     */
    @Nullable RecipeType<?> getRecipeType();

    /**
     * Set the burn time for the given ItemStack.
     * Setting it to 0 will prevent the item from being used as fuel, overriding vanilla's decision.
     */
    void setBurnTime(int burnTime);

    /**
     * The resulting value of this event, the burn time for the ItemStack.
     * A value of 0 will prevent the item from being used as fuel, overriding vanilla's decision.
     */
    int getBurnTime();
}
