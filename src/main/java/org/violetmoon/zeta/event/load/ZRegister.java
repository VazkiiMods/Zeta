package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.registry.VariantRegistry;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.registry.BrewingRegistry;
import org.violetmoon.zeta.registry.CraftingExtensionsRegistry;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;

@SuppressWarnings("ClassCanBeRecord")
public class ZRegister implements IZetaLoadEvent {
	public final Zeta zeta;

	public ZRegister(Zeta zeta) {
		this.zeta = zeta;
	}

	public ZetaRegistry getRegistry() {
		return zeta.registry;
	}

	public CraftingExtensionsRegistry getCraftingExtensionsRegistry() {
		return zeta.craftingExtensions;
	}

	public BrewingRegistry getBrewingRegistry() {
		return zeta.brewingRegistry;
	}

	public RenderLayerRegistry getRenderLayerRegistry() {
		return zeta.renderLayerRegistry;
	}

	public AdvancementModifierRegistry getAdvancementModifierRegistry() {
		return zeta.advancementModifierRegistry;
	}

	public VariantRegistry getVariantRegistry() {
		return zeta.variantRegistry;
	}

	public static class Post implements IZetaLoadEvent { }
}
