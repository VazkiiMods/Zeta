package org.violetmoon.zeta.event.play;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.util.ConversionUtil;
import org.violetmoon.zeta.util.ZetaToolActions;

public class FabricZBlock implements ZBlock {
    private final BlockEvent e;

    public FabricZBlock(BlockEvent e) {
        this.e = e;
    }

    @Override
    public LevelAccessor getLevel() {
        return e.getLevel();
    }

    @Override
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public BlockState getState() {
        return e.getState();
    }

    public static class Break extends FabricZBlock implements ZBlock.Break {
        private final BlockEvent.BreakEvent e;

        public Break(BlockEvent.BreakEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public Player getPlayer() {
            return e.getPlayer();
        }
    }

    public static class EntityPlace extends FabricZBlock implements ZBlock.EntityPlace {
        private final BlockEvent.EntityPlaceEvent e;

        public EntityPlace(BlockEvent.EntityPlaceEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public BlockState getPlacedBlock() {
            return e.getPlacedBlock();
        }
    }

    public static class BlockToolModification extends FabricZBlock implements ZBlock.BlockToolModification {
        private final BlockEvent.BlockToolModificationEvent e;

        public BlockToolModification(BlockEvent.BlockToolModificationEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public ZetaToolActions.ZetaToolAction getToolAction() {
            return ConversionUtil.forgeToZetaToolActions(e.getToolAction());
        }

        @Override
        public void setFinalState(@Nullable BlockState finalState) {
            e.setFinalState(finalState);
        }
    }
}
