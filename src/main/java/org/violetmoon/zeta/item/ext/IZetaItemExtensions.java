package org.violetmoon.zeta.item.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("deprecation") //forge ext
public interface IZetaItemExtensions {

	default InteractionResult onItemUseFirstZeta(ItemStack stack, UseOnContext context) {
		return InteractionResult.PASS;
	}

	default boolean isRepairableZeta(ItemStack stack) {
		return false;
	}

	default boolean onEntityItemUpdateZeta(ItemStack stack, ItemEntity ent) {
		return false;
	}

	default boolean doesSneakBypassUseZeta(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return false;
	}

	default boolean canEquipZeta(ItemStack stack, EquipmentSlot equipmentSlot, Entity ent) {
		return false;
	}

	default boolean isBookEnchantableZeta(ItemStack stack, ItemStack book) {
		return true;
	}

	@Nullable
	default String getArmorTextureZeta(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return null;
	}

	default int getMaxDamageZeta(ItemStack stack) {
		return stack.getItem().getMaxDamage();
	}

	default boolean canShearZeta(ItemStack stack) { //canPerformAction
		return stack.getItem() instanceof ShearsItem;
	}

	default int getEnchantmentValueZeta(ItemStack stack) {
		return stack.getItem().getEnchantmentValue();
	}

	default boolean canApplyAtEnchantingTableZeta(ItemStack stack, Enchantment enchantment) {
		return enchantment.category.canEnchant(stack.getItem());
	}

	default int getEnchantmentLevelZeta(ItemStack stack, Enchantment enchantment) {
		return EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
	}

	default Map<Enchantment, Integer> getAllEnchantmentsZeta(ItemStack stack) {
		return EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
	}

	default boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.equals(newStack);
	}

	//TODO: initCapabilities

	default int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return 0;
	}

	default <T extends LivingEntity> int damageItemZeta(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
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
