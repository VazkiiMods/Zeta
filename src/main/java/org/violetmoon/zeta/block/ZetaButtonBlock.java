package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.violetmoon.zeta.util.BooleanSuppliers;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public abstract class ZetaButtonBlock extends ButtonBlock implements IZetaBlock {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaButtonBlock(BlockSetType setType, int ticksToStayPressed, boolean arrowsCanPress, String regname, @Nullable ZetaModule module, Properties properties) {
		super(properties, setType, ticksToStayPressed, arrowsCanPress);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerBlock(this, regname, true);
		CreativeTabManager.addToCreativeTabNextTo(CreativeModeTabs.REDSTONE_BLOCKS, this, Blocks.STONE_BUTTON, false);
	}

	@NotNull
	@Override
	protected abstract SoundEvent getSound(boolean powered);

	@Override
	public ZetaButtonBlock setCondition(BooleanSupplier enabledSupplier) {
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
