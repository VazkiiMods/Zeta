package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;
import org.violetmoon.zeta.registry.IZetaItemColorProvider;
import org.violetmoon.zeta.registry.VariantRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaStairsBlock extends StairBlock implements IZetaBlock, IZetaBlockColorProvider {

	private final IZetaBlock parent;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaStairsBlock(IZetaBlock parent, @Nullable ResourceKey<CreativeModeTab> tab) {
		super(parent.getBlock()::defaultBlockState, VariantRegistry.realStateCopy(parent));

		this.parent = parent;

		ZetaModule module = parent.getModule();
		if(module == null)
			throw new IllegalArgumentException("Can only create ZetaStairsBlock with blocks belonging to a module"); //for various reasons

		String resloc = module.zeta.registryUtil.inheritQuark(parent, "%s_stairs");
		parent.getModule().zeta.registry.registerBlock(this, resloc, true);
		parent.getModule().zeta.renderLayerRegistry.mock(this, parent.getBlock());
		setCreativeTab(tab == null ? CreativeModeTabs.BUILDING_BLOCKS : tab, parent.getBlock(), false);
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return parent.getModule().zeta.blockExtensions.get(parentState).isFlammableZeta(parentState, world, pos, face);
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return parent.getModule().zeta.blockExtensions.get(parentState).getFlammabilityZeta(parentState, world, pos, face);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return parent.getModule();
	}

	@Override
	public ZetaStairsBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return parent.getModule().zeta.blockExtensions.get(state).getBeaconColorMultiplierZeta(state, world, pos, beaconPos);
	}

	@Override
	public @Nullable String getBlockColorProviderName() {
		return parent instanceof IZetaBlockColorProvider prov ? prov.getBlockColorProviderName() : null;
	}

	@Override
	public @Nullable String getItemColorProviderName() {
		return parent instanceof IZetaItemColorProvider prov ? prov.getItemColorProviderName() : null;
	}

}
