package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabHandler;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ZetaHangingSignItem extends HangingSignItem implements IZetaItem {
    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaHangingSignItem(@Nullable ZetaModule module, Block sign, Block wallSign) {
        super(sign, wallSign, new Item.Properties().stacksTo(16));

        this.module = module;

        if(module == null) //auto registration below this line
            return;

        Zeta zeta = module.zeta();
        String resloc = zeta.registryUtil.inherit(sign, "%s");
        zeta.registry.registerItem(this, resloc);
        zeta.creativeTabs.addToCreativeTabNextTo(CreativeModeTabs.FUNCTIONAL_BLOCKS, this, Items.CHEST, true);
    }

    @Override
    public ZetaHangingSignItem setCondition(BooleanSupplier enabledSupplier) {
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
