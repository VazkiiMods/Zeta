package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.registry.*;

public class ForgeZRegister extends Event implements ZRegister, IModBusEvent {

    private final Zeta zeta;

    public ForgeZRegister(Zeta zeta) {
        this.zeta = zeta;
    }

    @Override
    public ZetaRegistry getRegistry() {
        return zeta.registry;
    }

    @Override
    public CraftingExtensionsRegistry getCraftingExtensionsRegistry() {
        return zeta.craftingExtensions;
    }

    @Override
    public BrewingRegistry getBrewingRegistry() {
        return zeta.brewingRegistry;
    }

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
