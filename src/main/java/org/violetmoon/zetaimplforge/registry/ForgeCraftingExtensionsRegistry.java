package org.violetmoon.zetaimplforge.registry;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.violetmoon.zeta.recipe.IZetaCondition;
import org.violetmoon.zeta.recipe.IZetaConditionSerializer;
import org.violetmoon.zeta.recipe.IZetaIngredientSerializer;
import org.violetmoon.zeta.registry.CraftingExtensionsRegistry;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public class ForgeCraftingExtensionsRegistry implements CraftingExtensionsRegistry {

	/// Ingredient serializers ///

	//Used in IngredientMixin on Forge
	public final Map<IZetaIngredientSerializer<?>, IIngredientSerializer<?>> toForgeIngredientSerializers = new IdentityHashMap<>();

	@Override
	public <T extends Ingredient> IZetaIngredientSerializer<T> registerIngredientSerializer(ResourceLocation id, IZetaIngredientSerializer<T> serializer) {
		//Register a Forge ingredient serializer that delegates to our ingredient serializer.
		IIngredientSerializer<T> forge = new IIngredientSerializer<>() {
			@Override
			public T parse(FriendlyByteBuf buffer) {
				return serializer.parse(buffer);
			}

			@Override
			public T parse(JsonObject json) {
				return serializer.parse(json);
			}

			@Override
			public void write(FriendlyByteBuf buffer, T ingredient) {
				serializer.write(buffer, ingredient);
			}
		};

		CraftingHelper.register(id, forge);
		toForgeIngredientSerializers.put(serializer, forge);

		return serializer;
	}

	//TODO: Is getId needed?
	@Override
	public ResourceLocation getID(IZetaIngredientSerializer<?> serializer) {
		return CraftingHelper.getID(toForgeIngredientSerializers.get(serializer));
	}

	/// Condition serializers ///
	//These are far more annoying, since there's two layers of forge-specific classes to wrap

	//FORGE icontext to ZETA icontext
	public record Forge2ZetaContext(ICondition.IContext forge) implements IZetaCondition.IContext {
		@Override
		public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
			return forge.getAllTags(registry);
		}
	}

	//ZETA icondition to FORGE icondition
	public record Zeta2ForgeCondition<T extends IZetaCondition>(T zeta) implements ICondition {
		@Override
		public ResourceLocation getID() {
			return zeta.getID();
		}

		@Override
		public boolean test(ICondition.IContext context) {
			//Wrap the IContext in a class Zeta can refer to, before passing it to Zeta
			return zeta.test(new Forge2ZetaContext(context));
		}
	}

	@Override
	public <T extends IZetaCondition> IZetaConditionSerializer<T> registerConditionSerializer(IZetaConditionSerializer<T> serializer) {
		CraftingHelper.register(new IConditionSerializer<Zeta2ForgeCondition<T>>() {
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
		});

		return serializer;
	}
}
