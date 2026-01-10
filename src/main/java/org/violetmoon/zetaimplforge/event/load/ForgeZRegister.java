package org.violetmoon.zetaimplforge.event.load;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.registry.VariantRegistry;
import org.violetmoon.zeta.registry.ZetaRegistry;

public class ForgeZRegister extends Event implements ZRegister, IModBusEvent {

    private final Zeta zeta;

    public ForgeZRegister(Zeta zeta) {
        this.zeta = zeta;
    }

    @Override
    public ZetaRegistry getRegistry() {
        return zeta.registry;
    }

    //@Override
    /*public CraftingExtensionsRegistry getCraftingExtensionsRegistry() {
        return zeta.craftingExtensions;
    }*/

    @Override
    public RenderLayerRegistry getRenderLayerRegistry() {
        return zeta.renderLayerRegistry;
    }

    @Override
    public AdvancementModifierRegistry getAdvancementModifierRegistry() {
        return zeta.advancementModifierRegistry;
    }

    @Override
    public VariantRegistry getVariantRegistry() {
        return zeta.variantRegistry;
    }

    public static class Post extends Event implements ZRegister.Post, IModBusEvent { }
}
