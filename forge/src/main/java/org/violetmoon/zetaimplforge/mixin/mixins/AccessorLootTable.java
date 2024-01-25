package org.violetmoon.zetaimplforge.mixin.mixins;

import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(LootTable.class)
public interface AccessorLootTable {
    @Accessor("f_79109_")
    List<LootPool> zeta$getPools();
}
