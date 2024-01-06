package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.DoubleHighBlockItem;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.IZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaDoubleHighBlockItem extends DoubleHighBlockItem implements IZetaItem {

	private final @Nullable ZetaModule module;

	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaDoubleHighBlockItem(IZetaBlock baseBlock, Properties props) {
		super(baseBlock.getBlock(), props);

		this.module = baseBlock.getModule();
	}

	@Override
	public ZetaDoubleHighBlockItem setCondition(BooleanSupplier enabledSupplier) {
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
