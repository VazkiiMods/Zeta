package org.violetmoon.zeta.event.play;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;

public class FabricZAnvilUpdate implements ZAnvilUpdate {
    private final AnvilUpdateEvent e;

    public FabricZAnvilUpdate(AnvilUpdateEvent e) {
        this.e = e;
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
    public ItemStack getOutput() {
        return e.getOutput();
    }

    @Override
    public String getName() {
        return e.getName();
    }

    @Override
    public void setOutput(ItemStack output) {
        e.setOutput(output);
    }

    @Override
    public void setCost(int cost) {
        e.setCost(cost);
    }

    @Override
    public int getMaterialCost() {
        return e.getMaterialCost();
    }

    @Override
    public void setMaterialCost(int materialCost) {
        e.setMaterialCost(materialCost);
    }

    @Override
    public Player getPlayer() {
        return e.getPlayer();
    }

    public static class Lowest extends FabricZAnvilUpdate implements ZAnvilUpdate.Lowest {
        public Lowest(AnvilUpdateEvent e) {
            super(e);
        }
    }

    public static class Highest extends FabricZAnvilUpdate implements ZAnvilUpdate.Highest {
        public Highest(AnvilUpdateEvent e) {
            super(e);
        }
    }
}
