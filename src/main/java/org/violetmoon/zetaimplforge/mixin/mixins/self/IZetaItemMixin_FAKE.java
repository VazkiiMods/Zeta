package org.violetmoon.zetaimplforge.mixin.mixins.self;

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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.zeta.item.ZetaArmorItem;
import org.violetmoon.zeta.item.ZetaBlockItem;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;
import org.violetmoon.zeta.mixin.mixins.InvokerIItemStackExtension;

import java.util.function.Consumer;

// Forge can't actually mixin to interfaces, so we fake it by just... mixing in to everyone inheriting the interface.
@Mixin({
	ZetaArmorItem.class,
	ZetaBlockItem.class,
	ZetaItem.class,
})

// TODO: getArmorTexture() and getMaxDamage() no longer exist
public class IZetaItemMixin_FAKE implements IItemStackExtension, IZetaItemExtensions {

	@Override
	public InteractionResult onItemUseFirst(UseOnContext context) {
		return onItemUseFirstZeta(context);
	}

	@Override
	public boolean isRepairable() {
		return isRepairableZeta();
	}

	@Override
	public boolean onEntityItemUpdate(ItemEntity entity) {
		return onEntityItemUpdateZeta(entity);
	}

	@Override
	public boolean doesSneakBypassUse(LevelReader level, BlockPos pos, Player player) {
		return doesSneakBypassUseZeta(level, pos, player);
	}

	@Override
	public boolean canEquip(EquipmentSlot armorType, LivingEntity entity) {
		return canEquipZeta(armorType, entity);
	}

	@Override
	public boolean isBookEnchantable(ItemStack book) {
		return isBookEnchantableZeta(book);
	}

	@Override
	public boolean canPerformAction(ItemAbility itemAbility) {
		ItemStack stack = ((InvokerIItemStackExtension) this).zeta$getSelf();
		return itemAbility == ItemAbilities.SHEARS_CARVE && canShearZeta(stack);
	}

	@Override
	public int getEnchantmentValue() {
		return getEnchantmentValueZeta();
	}

	@Override
	public int getEnchantmentLevel(Holder<Enchantment> enchantment) {
		return getEnchantmentLevelZeta(enchantment);
	}

	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return getAllEnchantmentsZeta(stack, lookup);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return shouldCauseReequipAnimationZeta(oldStack, newStack, slotChanged);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return getBurnTimeZeta(itemStack, recipeType);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return damageItemZeta(stack, amount, entity, onBroken);
	}

	@Override
	public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
		return isEnderMaskZeta(stack, player, endermanEntity);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return canElytraFlyZeta(stack, entity);
	}

	@Override
	public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
		return getDefaultTooltipHideFlagsZeta(stack);
	}

}
