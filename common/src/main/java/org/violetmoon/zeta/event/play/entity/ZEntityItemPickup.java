package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.PlayerGetter;

import net.minecraft.world.entity.item.ItemEntity;

public interface ZEntityItemPickup extends IZetaPlayEvent, PlayerGetter {
    ItemEntity getItem();
}
