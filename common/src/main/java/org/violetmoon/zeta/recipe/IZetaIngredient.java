package org.violetmoon.zeta.recipe;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * Forge hacks some custom IIngredientSerializer stuff onto Ingredient
 */
public interface IZetaIngredient<T extends Ingredient> {
	IZetaIngredientSerializer<T> zetaGetSerializer();
}
