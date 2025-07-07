package org.violetmoon.zetaimplforge.mixin.mixins.self;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.zeta.item.IZetaItem;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;

import java.util.function.Consumer;

// The concept is evil, frankly.
@Mixin(IZetaItem.class)
public interface IZetaItemMixin extends IItemExtension, IZetaItemExtensions {
    @Override
    default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return onItemUseFirstZeta(stack, context);
    }

    @Override
    default boolean isRepairable(ItemStack stack) {
        return isRepairableZeta(stack);
    }

    @Override
    default boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return onEntityItemUpdateZeta(stack, entity);
    }

    @Override
    default boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return doesSneakBypassUseZeta(stack, level, pos, player);
    }

    @Override
    default boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return stack.canEquip(armorType, entity);
    }

    @Override
    default boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        boolean canEnchant = true;
        for (Holder<Enchantment> enchantment : book.get(DataComponents.ENCHANTMENTS).keySet()) {
            canEnchant = enchantment.value().canEnchant(stack);
            if (!canEnchant) break;
        }
        return canEnchant;
    }

    @Override
    default int getEnchantmentValue(ItemStack stack) {
        return getEnchantmentValueZeta(stack);
    }

    @Override
    default boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SHEARS_CARVE && canShearZeta(stack);
    }

    @Override
    default int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
        return getEnchantmentLevelZeta(stack, enchantment);
    }

    @Override
    default ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
        return getAllEnchantmentsZeta(stack, lookup);
    }

    @Override
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return shouldCauseReequipAnimationZeta(oldStack, newStack, slotChanged);
    }

    @Override
    default int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return getBurnTimeZeta(stack, recipeType);
    }

    @Override
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        return damageItemZeta(stack, amount, entity, onBroken);
    }

    @Override
    default boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return isEnderMaskZeta(stack, player, endermanEntity);
    }

    @Override
    default boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return canElytraFlyZeta(stack, entity);
    }
}
