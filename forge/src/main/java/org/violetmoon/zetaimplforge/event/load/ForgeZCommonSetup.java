package org.violetmoon.zetaimplforge.event.load;

import org.violetmoon.zeta.event.load.ZCommonSetup;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public record ForgeZCommonSetup(FMLCommonSetupEvent e) implements ZCommonSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
