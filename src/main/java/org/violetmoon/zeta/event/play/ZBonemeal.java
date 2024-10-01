package org.violetmoon.zeta.event.play;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import javax.annotation.Nullable;

public interface ZBonemeal extends IZetaPlayEvent, Cancellable {

    @Nullable Player getPlayer();
    Level getLevel();
    BlockPos getPos();
    BlockState getBlock();
    ItemStack getStack();
    boolean isValidBonemealTarget();
    void setSuccessful(boolean success);
    boolean isSuccessful();
    void setCanceled(boolean canceled);
}
