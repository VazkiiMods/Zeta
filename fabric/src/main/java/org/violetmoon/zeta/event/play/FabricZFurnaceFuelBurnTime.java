package org.violetmoon.zeta.event.play;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import org.jetbrains.annotations.NotNull;

public class FabricZFurnaceFuelBurnTime implements ZFurnaceFuelBurnTime {
	private final FurnaceFuelBurnTimeEvent e;

	public FabricZFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent e) {
		this.e = e;
	}

	@Override
	public @NotNull ItemStack getItemStack() {return e.getItemStack();}

	@Override
	public void setBurnTime(int burnTime) {e.setBurnTime(burnTime);}
}
