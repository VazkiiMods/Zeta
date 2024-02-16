package org.violetmoon.zeta.event.play.loading;

import java.util.List;

import org.violetmoon.zeta.event.play.loading.ZWandererTrades;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.WandererTradesEvent;

public class ForgeZWandererTrades implements ZWandererTrades {
    private final WandererTradesEvent e;

    public ForgeZWandererTrades(WandererTradesEvent e) {
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
