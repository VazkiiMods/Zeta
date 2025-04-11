package org.violetmoon.zeta.util.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZAddReloadListener;
import org.violetmoon.zeta.event.load.ZTagsUpdated;
import org.violetmoon.zeta.event.play.ZRecipeCrawl;
import org.violetmoon.zeta.event.play.ZServerTick;
import org.violetmoon.zeta.mod.ZetaMod;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@ApiStatus.Internal
public class RecipeCrawlHandler {

	// this just includes vanilla recipe types. Custom recipes could have some conversion scheme that we can't predict
	private static final List<Recipe<?>> vanillaRecipesToLazyDigest = new ArrayList<>();
	private static final Multimap<Item, ItemStack> vanillaRecipeDigestion = HashMultimap.create();
	private static final Multimap<Item, ItemStack> backwardsVanillaDigestion = HashMultimap.create();

	private static final Object mutex = new Object();
	private static boolean needsCrawl = false;
	private static boolean mayCrawl = false;

	@PlayEvent
	public static void addListener(ZAddReloadListener event) {
		event.addListener(new SimplePreparableReloadListener<Void>() {
			@Override
			protected Void prepare(ResourceManager mgr, ProfilerFiller prof) {
				clear();
				return null;
			}

			@Override
			protected void apply(Void what, ResourceManager mgr, ProfilerFiller prof) {
				needsCrawl = true;
			}
		});
	}

	@PlayEvent
	public static void tagsHaveUpdated(ZTagsUpdated event) {
		mayCrawl = true;
	}

	private static void clear() {
		mayCrawl = false;
		fire(new ZRecipeCrawl.Reset());
	}

	private static void fire(IZetaPlayEvent event) {
		ZetaMod.ZETA.playBus.fire(event);
	}

	@SuppressWarnings("ConstantValue")
	// some nullchecks on stuff that is ostensibly non-null, but you never know with mods
	private static void load(RecipeManager manager, RegistryAccess access) {
		if (!manager.getRecipes().isEmpty()) {
			fire(new ZRecipeCrawl.Starting());

			vanillaRecipesToLazyDigest.clear();
			vanillaRecipeDigestion.clear();
			backwardsVanillaDigestion.clear();

			//todo: Chat verify this works, and then clean it up
			for (RecipeHolder<?> recipeHolder : manager.getRecipes()) {
				try {
					Recipe<?> recipe = recipeHolder.value();
					if (recipe == null)
						throw new IllegalStateException("Recipe is null");
					if (recipe.getIngredients() == null)
						throw new IllegalStateException("Recipe ingredients are null");
					if (recipe.getResultItem(access) == null)
						throw new IllegalStateException("Recipe getResultItem is null");

					boolean isMisc = false;
					IZetaPlayEvent event;
					if (recipe instanceof ShapedRecipe sr)
						event = new ZRecipeCrawl.Visit.Shaped(sr, access);
					else if (recipe instanceof ShapelessRecipe sr)
						event = new ZRecipeCrawl.Visit.Shapeless(sr, access);
					else if (recipe instanceof CustomRecipe cr)
						event = new ZRecipeCrawl.Visit.Custom(cr, access);
					else if (recipe instanceof AbstractCookingRecipe acr)
						event = new ZRecipeCrawl.Visit.Cooking(acr, access);
					else {
						event = new ZRecipeCrawl.Visit.Misc(recipe, access);
						isMisc = true;
					}
					//misc recipes could have custom logic that we cant make many assumptions on. For example FD cutting board recipes are lossy.
					//for instance a hanging sign can be cut into a plank. A hanging sign is magnetic but this does not mean planks are
					if(!isMisc) {
						vanillaRecipesToLazyDigest.add(recipe);
					}
					fire(event);
				} catch (Exception e) {
					if (recipeHolder == null)
						Zeta.GLOBAL_LOG.error("Encountered null recipe in RecipeManager.getRecipes. This is not good");
					else
                        Zeta.GLOBAL_LOG.error("Failed to scan recipe {}. This should be reported to {}!", recipeHolder.id(), recipeHolder.id().getNamespace(), e);
				}
			}
		}
	}

	@PlayEvent
	public static void onTick(ZServerTick.Start tick) {
		synchronized (mutex) {
			if (mayCrawl && needsCrawl) {
				RecipeManager manager = tick.getServer().getRecipeManager();
				RegistryAccess access = tick.getServer().registryAccess();
				load(manager, access);
				needsCrawl = false;
			}

			if (!vanillaRecipesToLazyDigest.isEmpty()) {
				vanillaRecipeDigestion.clear();
				backwardsVanillaDigestion.clear();

				for (Recipe<?> recipe : vanillaRecipesToLazyDigest)
					digest(recipe, tick.getServer().registryAccess());

				vanillaRecipesToLazyDigest.clear();
				fire(new ZRecipeCrawl.Digest(vanillaRecipeDigestion, backwardsVanillaDigestion));
			}
		}
	}

	private static void digest(Recipe<?> recipe, RegistryAccess access) {
		// we only digest vanilla recipe types. Custom recipes could have some conversion scheme that we can't predict
		ItemStack out = recipe.getResultItem(access);
		Item outItem = out.getItem();

		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for (Ingredient ingredient : ingredients) {
			for (ItemStack inStack : ingredient.getItems()) {
				//don't include catalyst items. This includes partial ones like buckets and such
				ItemStack remaining = inStack.getCraftingRemainingItem();

				if(remaining == null) {
					//sigh. let's at least not make this into our problem
					ZetaMod.LOGGER.error("Item {} returned NULL from getCraftingRemainingItem. This is wrong and will cause problems down the line", inStack.getItem());
					continue;
				}

				if (remaining.isEmpty()) {
					vanillaRecipeDigestion.put(inStack.getItem(), out);
					backwardsVanillaDigestion.put(outItem, inStack);
				}
			}
		}
	}
}
