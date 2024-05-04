package org.violetmoon.zeta.event.play;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;

public class FabricZAnvilRepair implements ZAnvilRepair {
    private final AnvilRepairEvent e;

    public FabricZAnvilRepair(AnvilRepairEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public ItemStack getOutput() {
        return e.getOutput();
    }

    @Override
    public ItemStack getLeft() {
        return e.getLeft();
    }

    @Override
    public ItemStack getRight() {
        return e.getRight();
    }

    @Override
    public float getBreakChance() {
        return e.getBreakChance();
    }

    @Override
    public void setBreakChance(float breakChance) {
        e.setBreakChance(breakChance);
    }
}
