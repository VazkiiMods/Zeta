package org.violetmoon.zeta.recipe;

/**
 * Forge hacks some custom IIngredientSerializer stuff onto Ingredient
 */
public interface IZetaIngredient {
	IZetaCustomIngredient zetaGetSerializer();
}
