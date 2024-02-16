package org.violetmoon.zeta.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;

// haha yes im so good at naming stuff
public class ZetaWaxableStateBlock extends ZetaBlock {
    public static final BooleanProperty ZETA_WAXED = BooleanProperty.create("zeta_waxed");

    public ZetaWaxableStateBlock(String regname, @Nullable ZetaModule module, Properties properties) {
        super(regname, module, properties);
    }
}
