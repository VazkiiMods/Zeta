package org.violetmoon.zeta.util.forge;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.violetmoon.zeta.forge.mixin.mixins.AccessorLootTable;

import java.util.List;

public class MixinAbstractionsImpl {
    public static List<LootPool> LootPoolsAccessorAbstraction(LootTable lootTable) {
        return ((AccessorLootTable) lootTable).zeta$getPools();
    }
}
