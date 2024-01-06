package org.violetmoon.zeta.block;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.registry.IZetaBlockColorProvider;
import org.violetmoon.zeta.registry.IZetaItemColorProvider;

/**
 * @author WireSegal
 * Created at 1:09 PM on 9/19/19.
 */
public class ZetaInheritedPaneBlock extends ZetaPaneBlock implements IZetaBlock, IZetaBlockColorProvider {

	public final IZetaBlock parent;

	public ZetaInheritedPaneBlock(IZetaBlock parent, String name, Block.Properties properties) {
		super(name, parent.getModule(), properties, null);
		this.parent = parent;

		if(module == null || parent.getModule() == null) //auto registration below this line
			return;

		parent.getModule().zeta.renderLayerRegistry.mock(this, parent.getBlock());
	}

	public ZetaInheritedPaneBlock(IZetaBlock parent, Block.Properties properties) {
		this(parent, Objects.requireNonNull(parent.getModule(), "Can only use this constructor on blocks with a ZetaModule").zeta.registryUtil.inheritQuark(parent, "%s_pane"), properties);
	}

	public ZetaInheritedPaneBlock(IZetaBlock parent) {
		this(parent, Block.Properties.copy(parent.getBlock()));
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && parent.isEnabled();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplierZeta(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		BlockState parentState = parent.getBlock().defaultBlockState();
		return parent.getModule().zeta.blockExtensions.get(parentState).getBeaconColorMultiplierZeta(parentState, world, pos, beaconPos);
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
