package org.violetmoon.zeta.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.recipe.ZetaDyeRecipe;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

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

	// Copy of DyedItemColor
	public ItemStack dyeItem(ItemStack stack, List<DyeItem> dyes) {
        if (!isDyeable(stack)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = stack.copyWithCount(1);
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int i1 = 0;
            DyedItemColor dyeditemcolor = itemstack.get(DataComponents.DYED_COLOR);
            if (dyeditemcolor != null) {
                int j1 = FastColor.ARGB32.red(dyeditemcolor.rgb());
                int k1 = FastColor.ARGB32.green(dyeditemcolor.rgb());
                int l1 = FastColor.ARGB32.blue(dyeditemcolor.rgb());
                l += Math.max(j1, Math.max(k1, l1));
                i += j1;
                j += k1;
                k += l1;
                ++i1;
            }

            for (DyeItem dyeitem : dyes) {
                int j3 = dyeitem.getDyeColor().getTextureDiffuseColor();
                int i2 = FastColor.ARGB32.red(j3);
                int j2 = FastColor.ARGB32.green(j3);
                int k2 = FastColor.ARGB32.blue(j3);
                l += Math.max(i2, Math.max(j2, k2));
                i += i2;
                j += j2;
                k += k2;
                ++i1;
            }

            int l2 = i / i1;
            int i3 = j / i1;
            int k3 = k / i1;
            float f = (float) l / (float) i1;
            float f1 = (float) Math.max(l2, Math.max(i3, k3));
            l2 = (int) ((float) l2 * f / f1);
            i3 = (int) ((float) i3 * f / f1);
            k3 = (int) ((float) k3 * f / f1);
            int l3 = FastColor.ARGB32.color(0, l2, i3, k3);
            boolean flag = dyeditemcolor == null || dyeditemcolor.showInTooltip();
            itemstack.set(DataComponents.DYED_COLOR, new DyedItemColor(l3, flag));
            return itemstack;
        }
    }
}