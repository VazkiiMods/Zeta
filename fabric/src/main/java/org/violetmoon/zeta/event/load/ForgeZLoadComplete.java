package org.violetmoon.zeta.event.load;

import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public record ForgeZLoadComplete(FMLLoadCompleteEvent e) implements ZLoadComplete {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
