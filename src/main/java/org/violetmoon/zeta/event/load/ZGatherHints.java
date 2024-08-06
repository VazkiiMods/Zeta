package org.violetmoon.zeta.event.load;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.module.ZetaModule;

public interface ZGatherHints extends IZetaLoadEvent {

    void accept(ItemLike itemLike, Component extra);

    void hintItem(ItemLike itemLike, Object... extra);

    void hintItem(ItemLike itemLike, String key, Object... extra);

    void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm);

}
