package org.violetmoon.zeta.event.play;

import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

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
        ToolAction getToolAction();
        void setFinalState(@Nullable BlockState finalState);
    }
}
