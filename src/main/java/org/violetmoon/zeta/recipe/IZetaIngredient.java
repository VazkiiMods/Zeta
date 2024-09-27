package org.violetmoon.zeta.recipe;

import net.neoforged.neoforge.common.crafting.ICustomIngredient;

/**
 * Forge hacks some custom IIngredientSerializer stuff onto Ingredient
 */
public interface IZetaIngredient<T extends ICustomIngredient> {
	IZetaIngredientSerializer<T> zetaGetSerializer();
}
