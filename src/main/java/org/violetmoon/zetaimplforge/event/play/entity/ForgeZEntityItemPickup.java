package org.violetmoon.zetaimplforge.event.play.entity;

import org.violetmoon.zeta.event.play.entity.ZEntityItemPickup;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class ForgeZEntityItemPickup implements ZEntityItemPickup {
    private final EntityItemPickupEvent e;

    public ForgeZEntityItemPickup(EntityItemPickupEvent e) {
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
