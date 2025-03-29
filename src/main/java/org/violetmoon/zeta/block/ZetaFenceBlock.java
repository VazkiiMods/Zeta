package org.violetmoon.zeta.block;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.FenceBlock;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabHandler;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaFenceBlock extends FenceBlock implements IZetaBlock {

    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaFenceBlock(String regname, @Nullable ZetaModule module, Properties properties) {
        super(properties);
        this.module = module;

        if (module == null) //auto registration below this line
            return;

        Zeta zeta = module.zeta();
        zeta.registry.registerBlock(this, regname, true);
        zeta.creativeTabs.addToCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, this);
    }

    @Override
    public ZetaFenceBlock setCondition(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
        return this;
    }

    @Override
    public boolean doesConditionApply() {
        return enabledSupplier.getAsBoolean();
    }

    @Nullable
    @Override
    public ZetaModule getModule() {
        return module;
    }

}
