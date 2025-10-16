package org.violetmoon.zeta.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.recipe.ZetaDyeRecipe;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @see org.violetmoon.zeta.client.ClientRegistryExtension
 */
public class DyeablesRegistry {
	//todo: This needs a redo
	public final Map<Item, BooleanSupplier> dyeableConditions = new HashMap<>();
	//public final DyeableLeatherItem SURROGATE = new DyeableLeatherItem() {}; //Simply an accessor for various DyeableLeatherItem default methods

	@LoadEvent
	public void register(ZRegister event) {
		ResourceLocation id = event.getRegistry().newResourceLocation("dye_item");
		ZetaDyeRecipe recipe = new ZetaDyeRecipe(CraftingBookCategory.EQUIPMENT, this);
		event.getRegistry().register(recipe.getSerializer(), id, Registries.RECIPE_SERIALIZER);
	}

	@LoadEvent
	public void registerPost(ZRegister.Post event) {
		WashingInteraction wosh = new WashingInteraction();
		for(Item item : dyeableConditions.keySet())
			CauldronInteraction.WATER.map().put(item, wosh);
	}

	class WashingInteraction implements CauldronInteraction {
		//Copy of CauldronInteraction.DYED_ITEM
		@Override
		public @NotNull ItemInteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
			if(!isDyed(stack))
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

			if(!level.isClientSide) {
				stack.remove(DataComponents.DYED_COLOR);
				player.awardStat(Stats.CLEAN_ARMOR);
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	public void register(Item item) {
		register(item, BooleanSuppliers.TRUE);
	}

	public void register(Item item, ZetaModule module) {
		register(item, module::isEnabled);
	}

	public void register(Item item, BooleanSupplier cond) {
		dyeableConditions.put(item, cond);
	}

	public boolean isDyeable(ItemStack stack) {
		Item item = stack.getItem();
		return dyeableConditions.containsKey(item) && dyeableConditions.get(item).getAsBoolean();
	}

	public boolean isDyed(ItemStack stack) {
		return isDyeable(stack) && stack.getComponents().has(DataComponents.DYED_COLOR);
	}

	public DyedItemColor getDye(ItemStack stack) {
		return stack.getComponents().get(DataComponents.DYED_COLOR);
	}

	public void applyDye(ItemStack stack, DyedItemColor color) {
		if(isDyeable(stack))
			stack.update(DataComponents.DYED_COLOR, color,  dyedItemColor -> dyedItemColor);
	}

	public DyedItemColor getColor(ItemStack stack) {
		return isDyed(stack) ? stack.getComponents().get(DataComponents.DYED_COLOR) : new DyedItemColor(0xFF_FF_FF, false);
	}

	// Copy of DyeableLeatherItem
	public ItemStack dyeItem(ItemStack stack, List<DyeItem> dyes) {
		ItemStack itemstack;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;

		if(isDyeable(stack)) {
			itemstack = stack.copy();
			itemstack.setCount(1);
			if(stack.getComponents().has(DataComponents.DYED_COLOR)) {
				DyedItemColor k = stack.getComponents().get(DataComponents.DYED_COLOR);
				float f = (float) (k.rgb() >> 16 & 255) / 255.0F;
				float f1 = (float) (k.rgb() >> 8 & 255) / 255.0F;
				float f2 = (float) (k.rgb() & 255) / 255.0F;
				i += (int) (Math.max(f, Math.max(f1, f2)) * 255.0F);
				aint[0] += (int) (f * 255.0F);
				aint[1] += (int) (f1 * 255.0F);
				aint[2] += (int) (f2 * 255.0F);
				++j;
			}

			for(DyeItem dyeItem : dyes) {
				int diffuseColor = dyeItem.getDyeColor().getTextureDiffuseColor();
				float f = (diffuseColor >> 16 & 255) / 255.0F;
				float f1 = (diffuseColor >> 8 & 255) / 255.0F;
				float f2 = (diffuseColor & 255) / 255.0F;
				i += (int) Math.max(f2, Math.max(f, f1));
				aint[0] += (int) f;
				aint[1] += (int) f1;
				aint[2] += (int) f2;
				++j;
			}

			int j1 = aint[0] / j;
			int k1 = aint[1] / j;
			int l1 = aint[2] / j;
			float f3 = (float) i / (float) j;
			float f4 = (float) Math.max(j1, Math.max(k1, l1));
			j1 = (int) ((float) j1 * f3 / f4);
			k1 = (int) ((float) k1 * f3 / f4);
			l1 = (int) ((float) l1 * f3 / f4);
			int rgb = (j1 << 8) + k1;
			rgb = (rgb << 8) + l1;
			stack.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb, false));

			return itemstack;
		}

		return ItemStack.EMPTY;
	}

}
