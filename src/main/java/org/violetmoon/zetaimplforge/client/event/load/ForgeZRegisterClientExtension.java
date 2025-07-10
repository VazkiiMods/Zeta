package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.violetmoon.zeta.client.event.load.ZRegisterClientExtension;
import org.violetmoon.zeta.client.extensions.IZetaClientItemExtensions;

public record ForgeZRegisterClientExtension(RegisterClientExtensionsEvent event) implements ZRegisterClientExtension {
    @Override
    public void registerItem(IZetaClientItemExtensions extension, Item item) {
            event.registerItem(new IClientItemExtensions() {
                public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                    if (extension.getHumanoidArmorModel() != null)
                        return extension.getHumanoidArmorModel().getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
                    else return original;
                }

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return extension.getBEWLR();
                }
            }, item
        );
    }
}
