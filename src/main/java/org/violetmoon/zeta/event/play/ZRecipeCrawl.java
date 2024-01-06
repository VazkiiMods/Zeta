package org.violetmoon.zeta.event.play;

import java.util.Collection;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public abstract class ZRecipeCrawl implements IZetaPlayEvent {

	public static class Reset extends ZRecipeCrawl { }
	public static class Starting extends ZRecipeCrawl { }

	public static class Digest extends ZRecipeCrawl {

		private final Multimap<Item, ItemStack> digestion;
		private final Multimap<Item, ItemStack> backwardsDigestion;

		public Digest(Multimap<Item, ItemStack> digestion, Multimap<Item, ItemStack> backwardsDigestion) {
			this.digestion = digestion;
			this.backwardsDigestion = backwardsDigestion;
		}

		public boolean has(Item item, boolean backwards) {
			return (backwards ? backwardsDigestion : digestion).containsKey(item);
		}

		public Collection<ItemStack> get(Item item, boolean backwards) {
			return (backwards ? backwardsDigestion : digestion).get(item);
		}

	}

	public static abstract class Visit<T extends Recipe<?>> extends ZRecipeCrawl {

		public final T recipe;
		public final ResourceLocation recipeID;
		public final ItemStack output;
		public final NonNullList<Ingredient> ingredients;

		public Visit(T recipe, RegistryAccess access) {
			this.recipe = recipe;
			this.recipeID = recipe.getId();
			this.output = recipe.getResultItem(access);
			this.ingredients = recipe.getIngredients();
		}

		public static class Shaped extends Visit<ShapedRecipe> {

			public Shaped(ShapedRecipe recipe, RegistryAccess access) {
				super(recipe, access);
			}

		}

		public static class Shapeless extends Visit<ShapelessRecipe> {

			public Shapeless(ShapelessRecipe recipe, RegistryAccess access) {
				super(recipe, access);
			}

		}

		public static class Custom extends Visit<CustomRecipe> {

			public Custom(CustomRecipe recipe, RegistryAccess access) {
				super(recipe, access);
			}

		}

		public static class Cooking extends Visit<AbstractCookingRecipe> {

			public Cooking(AbstractCookingRecipe recipe, RegistryAccess access) {
				super(recipe, access);
			}

		}

		public static class Misc extends Visit<Recipe<?>> {

			public Misc(Recipe<?> recipe, RegistryAccess access) {
				super(recipe, access);
			}

		}

	}
}
