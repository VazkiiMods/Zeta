package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.registry.VariantRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;

@SuppressWarnings("ClassCanBeRecord")
public interface ZRegister extends IZetaLoadEvent {

	ZetaRegistry getRegistry();

	RenderLayerRegistry getRenderLayerRegistry();

	AdvancementModifierRegistry getAdvancementModifierRegistry();

	VariantRegistry getVariantRegistry();

	interface Post extends IZetaLoadEvent { }
}
