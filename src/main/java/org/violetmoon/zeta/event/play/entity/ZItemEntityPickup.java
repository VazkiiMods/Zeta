package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

import net.minecraft.world.entity.item.ItemEntity;

/**
 * Event for when specifically a PLAYER picks up an Item.
 * Other mobs who pick up items (Zombies, Skeletons, etc.) are not seemingly detected by this event.
 * Sorry...
 */
public interface ZItemEntityPickup extends IZetaPlayEvent, PlayerGetter {
    ItemEntity getItem();
}
