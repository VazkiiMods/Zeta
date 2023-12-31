package org.violetmoon.zetaimplforge.client.event.load;

import org.violetmoon.zeta.client.event.load.ZClientSetup;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public record ForgeZClientSetup(FMLClientSetupEvent e) implements ZClientSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
