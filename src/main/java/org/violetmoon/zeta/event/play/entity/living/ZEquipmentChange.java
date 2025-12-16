package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

public interface ZEquipmentChange extends IZetaPlayEvent, LivingGetter {
    EquipmentSlot getSlot();
    ItemStack getFrom();
    ItemStack getTo();
}
