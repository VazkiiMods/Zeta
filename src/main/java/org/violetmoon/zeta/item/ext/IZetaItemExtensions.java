package org.violetmoon.zeta.item.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IZetaItemExtensions {

	private Item self() {
		return (Item)this;
	}

	default InteractionResult onItemUseFirstZeta(ItemStack stack, UseOnContext context) {
		return InteractionResult.PASS;
	}

	default boolean isRepairableZeta(ItemStack stack) {
		return false;
	}

	default boolean onEntityItemUpdateZeta(ItemStack stack, ItemEntity entity) {
		return false;
	}

	default boolean doesSneakBypassUseZeta(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return false;
	}

	default boolean canEquipZeta(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return false;
	}

	default boolean isBookEnchantableZeta(ItemStack stack, ItemStack book) {
		return true;
	}

	default int getEnchantmentValueZeta(ItemStack stack) {
		return 0;
	}

	default boolean canShearZeta(ItemStack stack) { //canPerformAction
		return stack.getItem() instanceof ShearsItem;
	}

	default int getEnchantmentLevelZeta(ItemStack stack, Holder<Enchantment> enchantment) {
		ItemEnchantments itemenchantments = stack.getTagEnchantments();
		return itemenchantments.getLevel(enchantment);
	}

	@Deprecated
	default ItemEnchantments getAllEnchantmentsZeta(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return stack.get(DataComponents.ENCHANTMENTS);
	}

	default boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.equals(newStack);
	}

	// IItemExtension#getBurnTime is annotated as OverrideOnly for some reason, hardcode to be safe for now
	default int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		FurnaceFuel furnaceFuel = stack.getItem().builtInRegistryHolder().getData(NeoForgeDataMaps.FURNACE_FUELS);
		return furnaceFuel == null ? 0 : furnaceFuel.burnTime();
	}

	default <T extends LivingEntity> int damageItemZeta(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return amount;
	}

	default boolean isEnderMaskZeta(ItemStack stack, Player player, EnderMan enderboy) {
		return stack.getItem() == Items.CARVED_PUMPKIN;
	}

	default boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
		return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
	}
}
