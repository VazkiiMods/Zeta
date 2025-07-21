package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;
import org.violetmoon.zeta.event.play.ZSkipAttributeTooltip;

public class ForgeZSkipAttributeTooltip implements ZSkipAttributeTooltip {
    public final GatherSkippedAttributeTooltipsEvent e;

    public ForgeZSkipAttributeTooltip(GatherSkippedAttributeTooltipsEvent e) {
        this.e = e;
    }


    @Override
    public void skipID(ResourceLocation id) {
        e.skipId(id);
    }

    @Override
    public void skipEquipmentGroup(EquipmentSlotGroup group) {
        e.skipGroup(group);
    }

    @Override
    public void setSkipAll(boolean skip) {
        e.setSkipAll(skip);
    }



    @Override
    public boolean getSkipAll() {
        return e.isSkippingAll();
    }

    @Override
    public boolean isSkipping(ResourceLocation id) {
        return e.isSkipped(id);
    }

    @Override
    public boolean isSkipping(EquipmentSlotGroup group) {
        return e.isSkipped(group);
    }
}
