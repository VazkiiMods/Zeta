package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

/**
 * @author WireSegal
 * Created at 9:41 PM on 10/8/19.
 */
public class ZetaPressurePlateBlock extends PressurePlateBlock implements IZetaBlock {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaPressurePlateBlock(Sensitivity sensitivity, String regname, @Nullable ZetaModule module, Properties properties, BlockSetType blockSetType) {
		super(sensitivity, properties, blockSetType);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerBlock(this, regname, true);
		setCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, false);
	}

	@Override
	public ZetaPressurePlateBlock setCondition(BooleanSupplier enabledSupplier) {
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
