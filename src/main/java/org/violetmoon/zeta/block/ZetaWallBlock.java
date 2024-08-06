package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;
import org.violetmoon.zeta.registry.IZetaItemColorProvider;
import org.violetmoon.zeta.registry.VariantRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ZetaWallBlock extends WallBlock implements IZetaBlock, IZetaBlockColorProvider {

	private final IZetaBlock parent;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaWallBlock(IZetaBlock parent, @Nullable ResourceKey<CreativeModeTab> tab) {
		super(VariantRegistry.realStateCopy(parent));

		this.parent = parent;

		ZetaModule module = parent.getModule();
		if(module == null)
			throw new IllegalArgumentException("Can only create ZetaWallBlock with blocks belonging to a module"); //getBeaconColorMultiplierZeta

		String resloc = module.zeta().registryUtil.inheritQuark(parent, "%s_wall");
		parent.getModule().zeta().registry.registerBlock(this, resloc, true);
		parent.getModule().zeta().renderLayerRegistry.mock(this, parent.getBlock());
		setCreativeTab(tab == null ? CreativeModeTabs.BUILDING_BLOCKS : tab, parent.getBlock(), false);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return parent.getModule();
	}

	@Override
	public ZetaWallBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return parent.getModule().zeta().blockExtensions.get(parentState).getBeaconColorMultiplierZeta(parentState, world, pos, beaconPos);
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
