package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.event.play.entity.player.ZPlayerInteract;
import org.violetmoon.zetaimplforge.mixin.mixins.AccessorEvent;

public class ForgeZPlayerInteract implements ZPlayerInteract {
    private final PlayerInteractEvent e;

    public ForgeZPlayerInteract(PlayerInteractEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public InteractionHand getHand() {
        return e.getHand();
    }

    @Override
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
    }

    @Override
    public boolean isCanceled() {
        return ((AccessorEvent)e).zeta$isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        ((AccessorEvent)e).zeta$setCanceled(cancel);
    }

    public static class EntityInteractSpecific extends ForgeZPlayerInteract implements ZPlayerInteract.EntityInteractSpecific {
        private final PlayerInteractEvent.EntityInteractSpecific e;

        public EntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
            super(e);
            this.e = e;
        }

        @Override
        public Entity getTarget() {
            return e.getTarget();
        }

        @Override
        public void setCancellationResult(InteractionResult result) {
            e.setCancellationResult(result);
        }
    }

    public static class EntityInteract extends ForgeZPlayerInteract implements ZPlayerInteract.EntityInteract {
        private final PlayerInteractEvent.EntityInteract e;

        public EntityInteract(PlayerInteractEvent.EntityInteract e) {
            super(e);
            this.e = e;
        }

        @Override
        public Entity getTarget() {
            return e.getTarget();
        }

        @Override
        public void setCancellationResult(InteractionResult result) {
            e.setCancellationResult(result);
        }
    }

    public static class RightClickBlock extends ForgeZPlayerInteract implements ZPlayerInteract.RightClickBlock {
        private final PlayerInteractEvent.RightClickBlock e;

        public RightClickBlock(PlayerInteractEvent.RightClickBlock e) {
            super(e);
            this.e = e;
        }

        @Override
        public void setCancellationResult(InteractionResult result) {
            e.setCancellationResult(result);
        }
    }

    public static class RightClickItem extends ForgeZPlayerInteract implements ZPlayerInteract.RightClickItem {
        private final PlayerInteractEvent.RightClickItem e;

        public RightClickItem(PlayerInteractEvent.RightClickItem e) {
            super(e);
            this.e = e;
        }

        @Override
        public ItemStack getItemStack() {
            return e.getItemStack();
        }

        @Override
        public void setCancellationResult(InteractionResult result) {
            e.setCancellationResult(result);
        }
    }
}
