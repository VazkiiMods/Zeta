package org.violetmoon.zeta.client.event.load;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;

public record ForgeZAddModels(ModelEvent.RegisterAdditional e) implements ZAddModels {
	@Override
	public void register(ResourceLocation model) {
		e.register(model);
	}
}
