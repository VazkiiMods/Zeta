package org.violetmoon.zeta.event.play.loading;

import java.util.List;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.npc.VillagerTrades;

public interface ZWandererTrades extends IZetaPlayEvent {
    List<VillagerTrades.ItemListing> getGenericTrades();
    List<VillagerTrades.ItemListing> getRareTrades();
}
