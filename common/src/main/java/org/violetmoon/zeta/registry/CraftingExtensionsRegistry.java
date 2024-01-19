package org.violetmoon.zeta.registry;

import org.violetmoon.zeta.recipe.IZetaConditionSerializer;
import org.violetmoon.zeta.recipe.IZetaIngredientSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public interface CraftingExtensionsRegistry {
	// yes the generic is bad but blame forge
	IZetaConditionSerializer<?> registerConditionSerializer(IZetaConditionSerializer<?> serializer);

	<T extends Ingredient> IZetaIngredientSerializer<T> registerIngredientSerializer(ResourceLocation id, IZetaIngredientSerializer<T> serializer);
	ResourceLocation getID(IZetaIngredientSerializer<?> serializer);
}
