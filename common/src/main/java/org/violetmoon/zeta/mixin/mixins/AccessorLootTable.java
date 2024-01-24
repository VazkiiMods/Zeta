package org.violetmoon.zeta.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootTable.class)
public interface AccessorLootTable {
	//fixme doesnt exist on forge and needs separate impl's
	@Accessor("pools")
	LootPool[] zeta$getPools();
}
