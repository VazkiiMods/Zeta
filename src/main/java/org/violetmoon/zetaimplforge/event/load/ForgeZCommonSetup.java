package org.violetmoon.zetaimplforge.event.load;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;

public record ForgeZCommonSetup(FMLCommonSetupEvent e) implements ZCommonSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
