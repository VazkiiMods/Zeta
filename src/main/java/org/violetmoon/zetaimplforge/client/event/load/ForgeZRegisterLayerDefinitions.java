package org.violetmoon.zetaimplforge.client.event.load;

import java.util.function.Supplier;

import org.violetmoon.zeta.client.event.load.ZRegisterLayerDefinitions;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;

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
