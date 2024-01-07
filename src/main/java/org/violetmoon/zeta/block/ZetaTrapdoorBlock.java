package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaTrapdoorBlock extends TrapDoorBlock implements IZetaBlock {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaTrapdoorBlock(BlockSetType setType, String regname, ZetaModule module, Properties properties) {
		super(properties, setType);
		this.module = module;

		if(module == null) //auto registration below this line
			throw new IllegalArgumentException("Must provide a module for ZetaTrapdoorBlock"); //isLadderZeta

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		module.zeta.registry.registerBlock(this, regname, true);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, this);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS, this);
	}

	@Override
	public boolean isLadderZeta(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		if(state.getValue(OPEN)) {
			BlockPos downPos = pos.below();
			BlockState down = level.getBlockState(downPos);
			return module.zeta.blockExtensions.get(down).makesOpenTrapdoorAboveClimbableZeta(down, level, downPos, state);
		} else return false;
	}

	@Override
	public ZetaTrapdoorBlock setCondition(BooleanSupplier enabledSupplier) {
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
