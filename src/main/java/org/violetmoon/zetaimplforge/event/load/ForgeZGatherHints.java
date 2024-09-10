package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.load.ZGatherHints;
import org.violetmoon.zeta.module.ZetaModule;

public class ForgeZGatherHints extends Event implements ZGatherHints, IModBusEvent {
    private final ZGatherHints wrapped;

    public ForgeZGatherHints(ZGatherHints e) {
        this.wrapped = e;
    }

    @Override
    public void accept(ItemLike itemLike, Component extra) {
        wrapped.accept(itemLike, extra);
    }

    @Override
    public void hintItem(ItemLike itemLike, Object... extra) {
        wrapped.hintItem(itemLike, extra);
    }

    @Override
    public void hintItem(ItemLike itemLike, String key, Object... extra) {
        wrapped.hintItem(itemLike, key, extra);
    }

    @Override
    public void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm) {
        wrapped.gatherHintsFromModule(module, cfm);
    }

    @Override
    public RegistryAccess getRegistryAccess() {
        return wrapped.getRegistryAccess();
    }
}
