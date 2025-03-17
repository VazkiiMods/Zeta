package org.violetmoon.zeta.block;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaSaplingBlock extends SaplingBlock implements IZetaBlock {

    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaSaplingBlock(String name, @Nullable ZetaModule module, AbstractTreeGrower tree) {
        super(tree, Block.Properties.copy(Blocks.OAK_SAPLING));
        this.module = module;

        if (module == null) //auto registration below this line
            return;

        Zeta zeta = module.zeta();
        zeta.registry.registerBlock(this, name + "_sapling", true);
        zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
        zeta.creativeTabs.addToCreativeTabNextTo(CreativeModeTabs.NATURAL_BLOCKS, this, Blocks.AZALEA, true);
    }

    @Nullable
    @Override
    public ZetaModule getModule() {
        return module;
    }

    @Override
    public ZetaSaplingBlock setCondition(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
        return this;
    }

    @Override
    public boolean doesConditionApply() {
        return enabledSupplier.getAsBoolean();
    }

}
