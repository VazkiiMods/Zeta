package org.violetmoon.zetaimplforge.item;

import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

public class IForgeItemItemExtensions implements IZetaItemExtensions {
	public static final IForgeItemItemExtensions INSTANCE = new IForgeItemItemExtensions();

	@Override
	public InteractionResult onItemUseFirstZeta(ItemStack stack, UseOnContext context) {
		return stack.onItemUseFirst(context);
	}

	@Override
	public boolean isRepairableZeta(ItemStack stack) {
		return stack.isRepairable();
	}

	@Override
	public boolean onEntityItemUpdateZeta(ItemStack stack, ItemEntity ent) {
		return stack.onEntityItemUpdate(ent);
	}

	@Override
	public boolean doesSneakBypassUseZeta(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return stack.doesSneakBypassUse(level, pos, player);
	}

	@Override
	public boolean canEquipZeta(ItemStack stack, EquipmentSlot armorType, Entity ent) {
		return stack.canEquip(armorType, ent);
	}

	@Override
	public boolean isBookEnchantableZeta(ItemStack stack, ItemStack book) {
		return stack.isBookEnchantable(book);
	}

	@Override
	public @Nullable String getArmorTextureZeta(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return stack.getItem().getArmorTexture(stack, entity, slot, type);
	}

	@Override
	public int getMaxDamageZeta(ItemStack stack) {
		return stack.getMaxDamage();
	}

	@Override
	public boolean canShearZeta(ItemStack stack) {
		return stack.canPerformAction(ToolActions.SHEARS_CARVE);
	}

	@Override
	public int getEnchantmentValueZeta(ItemStack stack) {
		return stack.getItem().getEnchantmentValue(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTableZeta(ItemStack stack, Enchantment enchantment) {
		return stack.canApplyAtEnchantingTable(enchantment);
	}

	@Override
	public int getEnchantmentLevelZeta(ItemStack stack, Enchantment enchantment) {
		return stack.getEnchantmentLevel(enchantment);
	}

	@Override
	public Map<Enchantment, Integer> getAllEnchantmentsZeta(ItemStack stack) {
		return stack.getAllEnchantments();
	}

	@Override
	public boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem().shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public int getBurnTimeZeta(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return stack.getBurnTime(recipeType);
	}

	@Override
	public <T extends LivingEntity> int damageItemZeta(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return stack.getItem().damageItem(stack, amount, entity, onBroken);
	}

	@Override
	public boolean isEnderMaskZeta(ItemStack stack, Player player, EnderMan enderboy) {
		return stack.isEnderMask(player, enderboy);
	}

	@Override
	public boolean canElytraFlyZeta(ItemStack stack, LivingEntity entity) {
		return stack.canElytraFly(entity);
	}

	@Override
	public int getDefaultTooltipHideFlagsZeta(@NotNull ItemStack stack) {
		return stack.getItem().getDefaultTooltipHideFlags(stack);
	}
}
