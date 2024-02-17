package org.violetmoon.zeta.forge.mixin.mixins;

import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(LootTable.class)
public interface AccessorLootTable {
    @Accessor(value = "pools", remap = false)
    List<LootPool> zeta$getPools();
}
