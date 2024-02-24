package org.violetmoon.zeta.event.load;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public record ForgeZCommonSetup(FMLCommonSetupEvent e) implements ZCommonSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
