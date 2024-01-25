package org.violetmoon.zeta.util.fabric;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

public class MixinAbstractionsImpl {
    public static List<LootPool> LootPoolsAccessorAbstraction(LootTable lootTable) {
        throw new RuntimeException("Not implemented yet");
    }
}
