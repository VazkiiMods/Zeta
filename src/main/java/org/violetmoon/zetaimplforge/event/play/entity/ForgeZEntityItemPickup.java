package org.violetmoon.zetaimplforge.event.play.entity;

import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.violetmoon.zeta.event.play.entity.ZItemEntityPickup;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public class ForgeZEntityItemPickup implements ZItemEntityPickup {
    private final ItemEntityPickupEvent.Post e;

    public ForgeZEntityItemPickup(ItemEntityPickupEvent.Post e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getPlayer();
    }

    @Override
    public ItemEntity getItem() {
        return e.getItemEntity();
    }
}
