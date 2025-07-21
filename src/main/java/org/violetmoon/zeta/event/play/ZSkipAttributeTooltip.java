package org.violetmoon.zeta.event.play;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

//Note: We might want to add skipping a given item. Not in Neoforge so we would have to add it by hand.
public interface ZSkipAttributeTooltip extends IZetaPlayEvent {
    void skipID(ResourceLocation id);
    void skipEquipmentGroup(EquipmentSlotGroup group);

    void setSkipAll(boolean skip);
    boolean getSkipAll();

    boolean isSkipping(ResourceLocation id);
    boolean isSkipping(EquipmentSlotGroup group);
}
