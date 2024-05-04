package org.violetmoon.zeta.event.play.loading;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.WandererTradesEvent;

import java.util.List;

public class FabricZWandererTrades implements ZWandererTrades {
    private final WandererTradesEvent e;

    public FabricZWandererTrades(WandererTradesEvent e) {
        this.e = e;
    }


    @Override
    public List<VillagerTrades.ItemListing> getGenericTrades() {
        return e.getGenericTrades();
    }

    @Override
    public List<VillagerTrades.ItemListing> getRareTrades() {
        return e.getRareTrades();
    }
}
