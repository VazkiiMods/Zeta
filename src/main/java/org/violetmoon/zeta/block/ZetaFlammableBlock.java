package org.violetmoon.zeta.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;

public class ZetaFlammableBlock extends ZetaBlock {

	private final int flammability;

	public ZetaFlammableBlock(String regname, @Nullable ZetaModule module, int flamability, Properties properties) {
		super(regname, module, properties);
		this.flammability = flamability;
	}

	@Override
	public boolean isFlammableZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return flammability;
	}

}
