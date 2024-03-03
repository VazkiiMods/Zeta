package org.violetmoon.zeta.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

public class FabricClientRegistryExtension extends ClientRegistryExtension {
	public FabricClientRegistryExtension(Zeta z) {
		super(z);
	}

	@Override
	@SuppressWarnings("removal")
	protected void doSetRenderLayer(Block block, RenderLayerRegistry.Layer layer) {
		BlockRenderLayerMap.INSTANCE.putBlock(block, resolvedTypes.get(layer));
	}
}
