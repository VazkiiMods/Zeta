package org.violetmoon.zeta.event.play;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.util.ZetaToolActions;

public interface ZBlock extends IZetaPlayEvent {
    LevelAccessor getLevel();
    BlockPos getPos();
    BlockState getState();

    interface Break extends ZBlock {
        Player getPlayer();
    }

    interface EntityPlace extends ZBlock {
        BlockState getPlacedBlock();
    }

    interface BlockToolModification extends ZBlock {
        ZetaToolActions.ZetaToolAction getToolAction();
        void setFinalState(@Nullable BlockState finalState);
    }
}
