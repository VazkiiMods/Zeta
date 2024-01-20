package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {
    @Accessor("descriptionId")
    String zeta$getDescriptionId();

    @Accessor("descriptionId")
    void zeta$setDescriptionId(String descriptionId);
}
