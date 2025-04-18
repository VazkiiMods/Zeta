package org.violetmoon.zeta.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaVineBlock extends VineBlock implements IZetaBlock {

    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaVineBlock(@Nullable ZetaModule module, String name, boolean creative) {
        super(OldMaterials.replaceablePlant().noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE));
        this.module = module;

        if (module == null) //auto registration below this line
            return;

        Zeta zeta = module.zeta();
        zeta.registry.registerBlock(this, name, true);
        zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);

        if (creative) {
            zeta.creativeTabs.addToCreativeTab(CreativeModeTabs.NATURAL_BLOCKS, this);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource random) {
        tick(state, worldIn, pos, random);
    }

    @Override
    public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 300;
    }

    @Nullable
    @Override
    public ZetaModule getModule() {
        return module;
    }

    @Override
    public ZetaVineBlock setCondition(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
        return this;
    }

    @Override
    public boolean doesConditionApply() {
        return enabledSupplier.getAsBoolean();
    }

}
