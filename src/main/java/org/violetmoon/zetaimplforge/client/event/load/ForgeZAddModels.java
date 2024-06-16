package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.violetmoon.zeta.client.event.load.ZAddModels;

public record ForgeZAddModels(ModelEvent.RegisterAdditional e) implements ZAddModels {
	@Override
	public void register(ModelResourceLocation model) {
		e.register(model);
	}
}
