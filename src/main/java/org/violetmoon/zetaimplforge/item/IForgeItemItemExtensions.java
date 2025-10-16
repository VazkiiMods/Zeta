package org.violetmoon.zetaimplforge.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbilities;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

import java.util.function.Consumer;

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
	public boolean onEntityItemUpdateZeta(ItemStack stack, ItemEntity entity) {
		return stack.onEntityItemUpdate(entity);
	}

	@Override
	public boolean doesSneakBypassUseZeta(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return stack.doesSneakBypassUse(level, pos, player);
	}

	@Override
	public boolean canEquipZeta(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return stack.canEquip(armorType, entity);
	}

	@Override
	public boolean isBookEnchantableZeta(ItemStack stack, ItemStack book) {
		return stack.isBookEnchantable(book);
	}

	@Override
	public int getEnchantmentValueZeta(ItemStack stack) {
		return stack.getItem().getEnchantmentValue(stack);
	}

	@Override
	public boolean canShearZeta(ItemStack stack) {
		return stack.canPerformAction(ItemAbilities.SHEARS_CARVE);
	}

	@Override
	public int getEnchantmentLevelZeta(ItemStack stack, Holder<Enchantment> enchantment) {
		return stack.getItem().getEnchantmentLevel(stack, enchantment);
	}

	@Override
	public ItemEnchantments getAllEnchantmentsZeta(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return stack.getAllEnchantments(lookup);
	}

	@Override
	public boolean shouldCauseReequipAnimationZeta(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem().shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public <T extends LivingEntity> int damageItemZeta(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
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
}
