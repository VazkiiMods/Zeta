package org.violetmoon.zeta.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaSignItem extends SignItem implements IZetaItem {

    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaSignItem(@Nullable ZetaModule module, Block sign, Block wallSign) {
        super(new Item.Properties().stacksTo(16), sign, wallSign);
        this.module = module;

        if (module == null) //auto registration below this line
            return;

        Zeta zeta = module.zeta();
        String resloc = zeta.registryUtil.inherit(sign, "%s");
        zeta.registry.registerItem(this, resloc);
        zeta.creativeTabs.addToCreativeTabNextTo(CreativeModeTabs.FUNCTIONAL_BLOCKS, this, Blocks.CHEST, true);
    }

    @Override
    public ZetaSignItem setCondition(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
        return this;
    }

    @Nullable
    @Override
    public ZetaModule getModule() {
        return module;
    }

    @Override
    public boolean doesConditionApply() {
        return enabledSupplier.getAsBoolean();
    }

}
