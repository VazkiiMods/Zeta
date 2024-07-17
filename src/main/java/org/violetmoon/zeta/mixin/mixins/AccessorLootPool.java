package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.class)
public interface AccessorLootPool {

    @Accessor("entries")
    List<LootPoolEntryContainer> zeta$getLootPoolEntries();

    @Accessor("entries")
    void zeta$setLootPoolEntries(List<LootPoolEntryContainer> entries);
}
