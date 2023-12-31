package org.violetmoon.zeta.event.play.loading;

import java.util.List;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

public interface ZVillagerTrades extends IZetaPlayEvent {
    Int2ObjectMap<List<VillagerTrades.ItemListing>> getTrades();
    VillagerProfession getType();
}
