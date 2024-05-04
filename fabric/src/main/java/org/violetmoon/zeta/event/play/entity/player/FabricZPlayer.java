package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FabricZPlayer implements ZPlayer {
    private final PlayerEvent e;

    public FabricZPlayer(PlayerEvent e) {
        this.e = e;
    }
    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    public static class BreakSpeed extends FabricZPlayer implements ZPlayer.BreakSpeed {
        private final PlayerEvent.BreakSpeed e;

        public BreakSpeed(PlayerEvent.BreakSpeed e) {
            super(e);
            this.e = e;
        }

        @Override
        public BlockState getState() {
            return e.getState();
        }

        @Override
        public float getOriginalSpeed() {
            return e.getOriginalSpeed();
        }

        @Override
        public void setNewSpeed(float newSpeed) {
            e.setNewSpeed(newSpeed);
        }
    }

    public static class Clone extends FabricZPlayer implements ZPlayer.Clone {
        private final PlayerEvent.Clone e;

        public Clone(PlayerEvent.Clone e) {
            super(e);
            this.e = e;
        }

        @Override
        public Player getOriginal() {
            return e.getOriginal();
        }
    }

    public static class LoggedIn extends FabricZPlayer implements ZPlayer.LoggedIn {
        public LoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
            super(e);
        }
    }

    public static class LoggedOut extends FabricZPlayer implements ZPlayer.LoggedOut {
        public LoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
            super(e);
        }
    }
}
