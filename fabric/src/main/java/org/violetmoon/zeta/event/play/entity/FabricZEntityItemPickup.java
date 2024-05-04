package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class FabricZEntityItemPickup implements ZEntityItemPickup {
    private final EntityItemPickupEvent e;

    public FabricZEntityItemPickup(EntityItemPickupEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public ItemEntity getItem() {
        return e.getItem();
    }
}
