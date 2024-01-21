package org.violetmoon.zetaimplforge.client;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.block.Block;

public class ForgeClientRegistryExtension extends ClientRegistryExtension {
	public ForgeClientRegistryExtension(Zeta z) {
		super(z);
	}

	//Forge has some weirdo extension, they want you to use json or something.
	//Doing it from java is easier and more akin to how it happens on Fabric.
	@Override
	@SuppressWarnings("removal")
	protected void doSetRenderLayer(Block block, RenderLayerRegistry.Layer layer) {
		ItemBlockRenderTypes.setRenderLayer(block, resolvedTypes.get(layer));
	}
}
