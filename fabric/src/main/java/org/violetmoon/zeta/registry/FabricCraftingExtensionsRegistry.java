package org.violetmoon.zeta.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.violetmoon.zeta.recipe.IZetaCondition;
import org.violetmoon.zeta.recipe.IZetaConditionSerializer;
import org.violetmoon.zeta.recipe.IZetaIngredientSerializer;

import java.util.*;

public class FabricCraftingExtensionsRegistry implements CraftingExtensionsRegistry {

	/// Ingredient serializers ///

	//Forge equivalent is used in IngredientMixin on Forge. We will see if its needed here.
	public final List<IZetaIngredientSerializer<?>> ingredientSerializers = new LinkedList<>();

	@Override
	public <T extends Ingredient> IZetaIngredientSerializer<T> registerIngredientSerializer(ResourceLocation id, IZetaIngredientSerializer<T> serializer) {
		ingredientSerializers.add(serializer);
		return serializer;
	}

	//TODO: Is getId needed?
	@Override
	public ResourceLocation getID(IZetaIngredientSerializer<?> serializer) {
		return serializer.getID();
	}

	/// Condition serializers ///
	//These are far more annoying, since there's two layers of forge-specific classes to wrap

	@Override
	public <T extends IZetaCondition> IZetaConditionSerializer<T> registerConditionSerializer(IZetaConditionSerializer<T> serializer) {
		/*CraftingHelper.register(new IConditionSerializer<Zeta2ForgeCondition<T>>() {
			@Override
			public Zeta2ForgeCondition<T> read(JsonObject json) {
				//Wrap the condition in Forge-specific wrapping before passing it to Forge.
				return new Zeta2ForgeCondition<>(serializer.read(json));
			}

			@Override
			public void write(JsonObject json, Zeta2ForgeCondition<T> value) {
				//Unwrap the condition from its Forge-specific wrapping before passing it to `serializer`.
				serializer.write(json, value.zeta);
			}

			@Override
			public ResourceLocation getID() {
				return serializer.getID();
			}
		});*/

		return serializer;
	}
}