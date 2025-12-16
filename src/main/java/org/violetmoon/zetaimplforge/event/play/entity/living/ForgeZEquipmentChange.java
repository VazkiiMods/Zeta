package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.violetmoon.zeta.event.play.entity.living.ZEquipmentChange;

public record ForgeZEquipmentChange(LivingEquipmentChangeEvent event) implements ZEquipmentChange {
    @Override
    public EquipmentSlot getSlot() {
        return event.getSlot();
    }

    @Override
    public ItemStack getFrom() {
        return event.getFrom();
    }

    @Override
    public ItemStack getTo() {
        return event.getTo();
    }

    @Override
    public LivingEntity getEntity() {
        return event.getEntity();
    }
}
