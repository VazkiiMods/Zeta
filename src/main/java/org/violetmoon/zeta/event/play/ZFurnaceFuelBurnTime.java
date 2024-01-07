package org.violetmoon.zeta.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.item.ItemStack;

public interface ZFurnaceFuelBurnTime extends IZetaPlayEvent {
	ItemStack getItemStack();
	void setBurnTime(int time);
}
