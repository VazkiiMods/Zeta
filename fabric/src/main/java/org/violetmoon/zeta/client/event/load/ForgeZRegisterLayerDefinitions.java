package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public class ForgeZRegisterLayerDefinitions implements ZRegisterLayerDefinitions {
	private final EntityRenderersEvent.RegisterLayerDefinitions e;

	public ForgeZRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
		this.e = e;
	}

	@Override
	public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
		e.registerLayerDefinition(layerLocation, supplier);
	}
}
