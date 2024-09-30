package org.violetmoon.zeta.event.play.entity.player;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ZPlayerInteract extends IZetaPlayEvent, Cancellable {
    Player getEntity();
    InteractionHand getHand();
    BlockPos getPos();
    Level getLevel();

    interface EntityInteractSpecific extends ZPlayerInteract {
        Entity getTarget();
        void setCancellationResult(InteractionResult result);
    }

    interface EntityInteract extends ZPlayerInteract {
        Entity getTarget();
        void setCancellationResult(InteractionResult result);
    }

    interface RightClickBlock extends ZPlayerInteract {
        void setCancellationResult(InteractionResult result);
    }

    interface RightClickItem extends ZPlayerInteract {
        ItemStack getItemStack();
        void setCancellationResult(InteractionResult result);
    }
}
