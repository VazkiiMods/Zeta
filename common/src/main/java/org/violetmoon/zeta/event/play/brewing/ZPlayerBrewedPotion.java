package org.violetmoon.zeta.event.play.brewing;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.play.entity.player.ZPlayer;

/**
 * This event is called when a player picks up a potion from a brewing stand.
 */
public interface ZPlayerBrewedPotion extends ZPlayer {
    /**
     * The ItemStack of the potion.
     */
    @NotNull ItemStack getStack();
}
