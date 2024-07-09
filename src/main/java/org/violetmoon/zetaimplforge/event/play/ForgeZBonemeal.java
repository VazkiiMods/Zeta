package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.play.ZBonemeal;
import org.violetmoon.zetaimplforge.ForgeZeta;

public class ForgeZBonemeal implements ZBonemeal {
    private final BonemealEvent e;

    public ForgeZBonemeal(BonemealEvent e) {
        this.e = e;
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
    public ZResult getResult() {
        return ForgeZeta.from(e.isSuccessful());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(ForgeZeta.to(value));
    }
}
