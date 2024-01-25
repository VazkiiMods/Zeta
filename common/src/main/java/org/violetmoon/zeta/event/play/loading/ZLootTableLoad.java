package org.violetmoon.zeta.event.play.loading;

import java.util.List;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.mixin.mixins.AccessorLootPool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.violetmoon.zeta.util.MixinAbstractions;

public interface ZLootTableLoad extends IZetaPlayEvent, Cancellable {
	ResourceLocation getName();
	LootTable getTable();
	void setTable(LootTable table);

	default void add(LootPoolEntryContainer entry) {
		List<LootPool> pools = MixinAbstractions.LootPoolsAccessorAbstraction(getTable());
		if (!pools.isEmpty()) {
			LootPool firstPool = pools.get(0);
			LootPoolEntryContainer[] entries = ((AccessorLootPool) firstPool).zeta$getEntries();

			LootPoolEntryContainer[] newEntries = new LootPoolEntryContainer[entries.length + 1];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);

			newEntries[entries.length] = entry;
			((AccessorLootPool) firstPool).zeta$setEntries(newEntries);
		}
	}
}
