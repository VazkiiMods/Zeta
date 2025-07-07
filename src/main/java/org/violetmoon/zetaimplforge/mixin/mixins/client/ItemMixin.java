package org.violetmoon.zetaimplforge.mixin.mixins.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;
import org.violetmoon.zetaimplforge.client.IZetaForgeItemStuff;

//todo: Yeah this mixin doesnt work.
@Mixin(Item.class)
public class ItemMixin implements IZetaForgeItemStuff {

	@Unique
	private Object zeta$renderProperties;

	@Override
	public void zeta$setBlockEntityWithoutLevelRenderer(BlockEntityWithoutLevelRenderer bewlr) {
		if(zeta$renderProperties != null)
			throw new IllegalStateException("Cannot set both BlockEntityWithoutLevelRenderer and HumanoidArmorModel because zeta's api is bad");

		zeta$renderProperties = new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return bewlr;
			}
		};
	}

	@Override
	public void zeta$setHumanoidArmorModel(HumanoidArmorModelGetter getter) {
		if(zeta$renderProperties != null)
			throw new IllegalStateException("Cannot set both BlockEntityWithoutLevelRenderer and HumanoidArmorModel because zeta's api is bad");

		zeta$renderProperties = new IClientItemExtensions() {
			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				return getter.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
			}
		};
	}
}
