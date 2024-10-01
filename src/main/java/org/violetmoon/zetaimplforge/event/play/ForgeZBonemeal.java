package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.play.ZBonemeal;

public class ForgeZBonemeal implements ZBonemeal {

    private final BonemealEvent e;

    public ForgeZBonemeal(BonemealEvent e) {
        this.e = e;
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return e.getPlayer();
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
    }

    @Override
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public BlockState getBlock() {
        return e.getState();
    }

    @Override
    public ItemStack getStack() {
        return e.getStack();
    }

    @Override
    public boolean isValidBonemealTarget() {
        return e.isValidBonemealTarget();
    }

    @Override
    public void setSuccessful(boolean success) {
        e.setSuccessful(success);
    }

    @Override
    public boolean isSuccessful() {
        return e.isSuccessful();
    }

    @Override
    public boolean isCanceled() {
        return e.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        e.setCanceled(cancel);
    }
}