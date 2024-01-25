package org.violetmoon.zeta.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/**
 * Abstractions for when forge changes array's to list or anything of the alike.
 */
public class MixinAbstractions {
    @ExpectPlatform
    public static List<LootPool> LootPoolsAccessorAbstraction(LootTable lootTable) {
        throw new AssertionError();
    }
}
