package org.violetmoon.zeta.event.play;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import com.google.common.collect.Multimap;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.util.RegistryUtil;

public abstract class ZRecipeCrawl implements IZetaPlayEvent {

	public static class Reset extends ZRecipeCrawl { }
	public static class Starting extends ZRecipeCrawl { }

	public static abstract class Visit<T extends RecipeHolder<?>> extends ZRecipeCrawl {

		public final RecipeHolder<?> recipeHolder;
		public final Recipe<?> recipe;
		public final ResourceLocation recipeID;
		public final ItemStack output;
		public final NonNullList<Ingredient> ingredients;

		public Visit(RecipeHolder<?> recipeHolder, RegistryAccess access) {
			this.recipeHolder = recipeHolder;
			this.recipe = recipeHolder.value();
			this.recipeID = recipeHolder.id();
			this.output = recipeHolder.value().getResultItem(access);
			this.ingredients = recipeHolder.value().getIngredients();
		}

		public static class Shaped extends Visit<RecipeHolder<ShapedRecipe>> {

			public Shaped(RecipeHolder<ShapedRecipe> recipeHolder, RegistryAccess access) {
				super(recipeHolder, access);
			}

		}

		public static class Shapeless extends Visit<RecipeHolder<ShapelessRecipe>> {

			public Shapeless(RecipeHolder<ShapelessRecipe> recipeHolder, RegistryAccess access) {
				super(recipeHolder, access);
			}

		}

		public static class Custom extends Visit<RecipeHolder<CustomRecipe>> {

			public Custom(RecipeHolder<CustomRecipe> recipeHolder, RegistryAccess access) {
				super(recipeHolder, access);
			}

		}

		public static class Cooking extends Visit<RecipeHolder<AbstractCookingRecipe>> {

			public Cooking(RecipeHolder<ShapelessRecipe> recipeHolder, RegistryAccess access) {
				super(recipeHolder, access);
			}

		}

		public static class Misc extends Visit<RecipeHolder<Recipe<?>>> {

			public Misc(RecipeHolder<Recipe<?>> recipeHolder, RegistryAccess access) {
				super(recipeHolder, access);
			}

		}

	}

	/**
	 * Fired after all recipes have been digested
	 */
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

		/*
		 * Derivation list -> items to add and then derive (raw materials)
		 * Whitelist -> items to add and not derive from
		 * Blacklist -> items to ignore
		 */

		public void recursivelyFindCraftedItemsFromStrings(@Nullable Collection<String> derivationList, @Nullable Collection<String> whitelist, @Nullable Collection<String> blacklist, Consumer<Item> callback) {
			List<Item> parsedDerivationList = derivationList == null ? null : RegistryUtil.massRegistryGet(derivationList, BuiltInRegistries.ITEM);
			List<Item> parsedWhitelist = whitelist == null ? null : RegistryUtil.massRegistryGet(whitelist, BuiltInRegistries.ITEM);
			List<Item> parsedBlacklist = blacklist == null ? null : RegistryUtil.massRegistryGet(blacklist, BuiltInRegistries.ITEM);

			recursivelyFindCraftedItems(parsedDerivationList, parsedWhitelist, parsedBlacklist, callback);
		}

		public void recursivelyFindCraftedItems(@Nullable Collection<Item> derivationList, @Nullable Collection<Item> whitelist, @Nullable Collection<Item> blacklist, Consumer<Item> callback) {
			Collection<Item> trueDerivationList = derivationList == null ? Lists.newArrayList() : derivationList;
			Collection<Item> trueWhitelist = whitelist == null ? Lists.newArrayList() : whitelist;
			Collection<Item> trueBlacklist = blacklist == null ? Lists.newArrayList() : blacklist;

			Streams.concat(trueDerivationList.stream(), trueWhitelist.stream()).forEach(callback);

			Set<Item> scanned = Sets.newHashSet(trueDerivationList);
			List<Item> toScan = Lists.newArrayList(trueDerivationList);

			while(!toScan.isEmpty()) {
				Item scan = toScan.remove(0);

				if(digestion.containsKey(scan)) {
					for(ItemStack digestedStack : digestion.get(scan)) {
						Item candidate = digestedStack.getItem();

						if(!scanned.contains(candidate)) {
							scanned.add(candidate);
							toScan.add(candidate);

							if(!trueBlacklist.contains(candidate))
								callback.accept(candidate);
						}
					}
				}
			}
		}

	}

}
