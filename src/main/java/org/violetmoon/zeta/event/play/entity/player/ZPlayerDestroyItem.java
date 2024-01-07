package org.violetmoon.zeta.event.play.entity.player;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ZPlayerDestroyItem extends IZetaPlayEvent {
    Player getEntity();
    ItemStack getOriginal();
    InteractionHand getHand();
}
