package org.violetmoon.zeta.event.play.loading;

import net.minecraft.world.entity.npc.VillagerTrades;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import java.util.List;

public interface ZWandererTrades extends IZetaPlayEvent {
    List<VillagerTrades.ItemListing> getGenericTrades();
    List<VillagerTrades.ItemListing> getRareTrades();
}
