package org.violetmoon.zeta.recipe;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.*;
import org.violetmoon.zeta.registry.DyeablesRegistry;

import com.google.common.collect.Lists;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// copy of ArmorDyeRecipe
public class ZetaDyeRecipe extends CustomRecipe {
	protected final DyeablesRegistry dyeablesRegistry;
	protected final RecipeSerializer<?> serializer;

	public ZetaDyeRecipe(CraftingBookCategory cat, DyeablesRegistry dyeablesRegistry) {
		super(cat);
		this.dyeablesRegistry = dyeablesRegistry;
		// We need to plug the serializer into itself, so that when a fresh copy of this ZetaDyeRecipe is constructed
		// it will have the exact same serializer instance already registered in the recipe serializer registry.
		// Yeah, it's weird i know. Don't try this at home, it's like searching Google into Google.
		// Btw, the serializer can't be made `static` because constructing this recipe type requires a DyeablesRegistry.
		this.serializer = new SimpleCraftingRecipeSerializer<>((cat_) -> new ZetaDyeRecipe(cat_, dyeablesRegistry, this::getSerializer));
	}

	protected ZetaDyeRecipe(CraftingBookCategory cat, DyeablesRegistry dyeablesRegistry, Supplier<RecipeSerializer<?>> loopCloser) {
		super(cat);
		this.dyeablesRegistry = dyeablesRegistry;
		this.serializer = loopCloser.get();
	}

	@Override
	public boolean matches(CraftingInput craftingInput, Level level) {
		ItemStack itemstack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.newArrayList();

		for(int i = 0; i < craftingInput.size(); ++i) {
			ItemStack itemstack1 = craftingInput.getItem(i);
			if (!itemstack1.isEmpty()) {
				if (dyeablesRegistry.isDyeable(itemstack1)) { // <- changed
					if (!itemstack.isEmpty()) {
						return false;
					}

					itemstack = itemstack1;
				} else {
					if (!(itemstack1.getItem() instanceof DyeItem)) {
						return false;
					}

					list.add(itemstack1);
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider pissing) {
		List<DyeItem> list = Lists.newArrayList();
		ItemStack itemstack = ItemStack.EMPTY;

		for(int i = 0; i < input.size(); ++i) {
			ItemStack itemstack1 = input.getItem(i);
			if (!itemstack1.isEmpty()) {
				Item item = itemstack1.getItem();
				if (dyeablesRegistry.isDyeable(itemstack1)) { // <- changed
					if (!itemstack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemstack = itemstack1.copy();
				} else {
					if (!(item instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					list.add((DyeItem)item);
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty() ?
				dyeablesRegistry.dyeItem(itemstack, list) : // <- changed
				ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return serializer;
	}
}
