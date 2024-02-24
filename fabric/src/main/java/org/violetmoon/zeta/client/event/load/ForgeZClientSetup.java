package org.violetmoon.zeta.client.event.load;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public record ForgeZClientSetup(FMLClientSetupEvent e) implements ZClientSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
