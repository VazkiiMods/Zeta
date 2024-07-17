package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface AccessorItem {

    @Accessor("descriptionId")
    void zeta$setDescriptionID(String string);
}
