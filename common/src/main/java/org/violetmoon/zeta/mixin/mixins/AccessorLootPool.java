package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface AccessorLootPool {
    @Accessor("entries")
    LootPoolEntryContainer[] zeta$getEntries();

    @Accessor("entries")
    void zeta$setEntries(LootPoolEntryContainer[] entries);
}
