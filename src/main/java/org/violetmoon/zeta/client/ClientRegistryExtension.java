package org.violetmoon.zeta.client;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZLoadComplete;
import org.violetmoon.zeta.registry.DyeablesRegistry;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Just a grab-bag of stuff that needs to be done on the physical client
 */
public abstract class ClientRegistryExtension {
	protected final Zeta z;
	protected final ZetaRegistry registry;

	protected final Map<RenderLayerRegistry.Layer, RenderType> resolvedTypes = new EnumMap<>(RenderLayerRegistry.Layer.class);

	public ClientRegistryExtension(Zeta z) {
		this.z = z;
		this.registry = z.registry;

		resolvedTypes.put(RenderLayerRegistry.Layer.SOLID, RenderType.solid());
		resolvedTypes.put(RenderLayerRegistry.Layer.CUTOUT, RenderType.cutout());
		resolvedTypes.put(RenderLayerRegistry.Layer.CUTOUT_MIPPED, RenderType.cutoutMipped());
		resolvedTypes.put(RenderLayerRegistry.Layer.TRANSLUCENT, RenderType.translucent());
	}

	@LoadEvent
	public void registerItemColorHandlers(ZAddItemColorHandlers event) {
		DyeablesRegistry dyeables = z.dyeables;

		ClampedItemPropertyFunction isDyed = (stack, level, entity, i) -> dyeables.isDyed(stack) ? 1 : 0;
		ItemColor color = (stack, layer) -> layer == 0 ? dyeables.getColor(stack) : 0xFF_FF_FF;
		//apparently ItemPropertyFunctions are weird and can only be assigned to the minecraft: namespace
		ResourceLocation isDyedId = new ResourceLocation("minecraft", z.modid + "_dyed");

		for(Item item : dyeables.dyeableConditions.keySet()) {
			ItemProperties.register(item, isDyedId, isDyed);
			event.register(color, item);
		}
	}

	@LoadEvent
	public void registerRenderLayers(ZClientSetup event) {
		z.renderLayerRegistry.finalize(this::doSetRenderLayer);
	}


	//I hope this won't run on dedicated servers
	@LoadEvent
	public void onLoadComplete(ZLoadComplete event){
		z.registry.validateColorsProviders();
	}

	protected abstract void doSetRenderLayer(Block block, RenderLayerRegistry.Layer layer);
}
