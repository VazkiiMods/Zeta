package org.violetmoon.zeta.item.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IZetaItemExtensions {

	default InteractionResult onItemUseFirstZeta(UseOnContext context) {
		return InteractionResult.PASS;
	}

	default boolean isRepairableZeta() {
		return false;
	}

	default boolean onEntityItemUpdateZeta(ItemEntity entity) {
		return false;
	}

	default boolean doesSneakBypassUseZeta(LevelReader level, BlockPos pos, Player player) {
		return false;
	}

	default boolean canEquipZeta(EquipmentSlot armorType, LivingEntity entity) {
		return false;
	}

	default boolean isBookEnchantableZeta(ItemStack book) {
		return true;
	}

	default int getEnchantmentValueZeta() {
		return stack.getEnchantmentValue();
	}

	default boolean canShearZeta(ItemStack stack) { //canPerformAction
		return stack.getItem() instanceof ShearsItem;
	}

	default int getEnchantmentLevelZeta(Holder<Enchantment> enchantment) {
		return EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
	}

	default ItemEnchantments getAllEnchantmentsZeta(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return stack.getAllEnchantments(lookup);
	}

	default boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.equals(newStack);
	}

	//TODO: initCapabilities

	default int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return 0;
	}

	default <T extends LivingEntity> int damageItemZeta(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return amount;
	}

	default boolean isEnderMaskZeta(ItemStack stack, Player player, EnderMan enderboy) {
		return stack.getItem() == Items.CARVED_PUMPKIN;
	}

	default boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
		//forge has a funky little extension for this
		return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
	}

	default int getDefaultTooltipHideFlagsZeta(@NotNull ItemStack stack) {
		return 0;
	}
}
