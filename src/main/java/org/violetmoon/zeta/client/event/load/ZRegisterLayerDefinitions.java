package org.violetmoon.zeta.client.event.load;

import java.util.function.Supplier;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public interface ZRegisterLayerDefinitions extends IZetaLoadEvent {
	void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
}
