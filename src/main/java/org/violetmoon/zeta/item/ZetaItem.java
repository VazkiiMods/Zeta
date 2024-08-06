package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.world.item.Item;

public class ZetaItem extends Item implements IZetaItem, IZetaItemExtensions {
	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaItem(String regname, @Nullable ZetaModule module, Properties properties) {
		super(properties);

		this.module = module;
		if(module == null) //auto registration below this line
			return;

		module.zeta().registry.registerItem(this, regname);
	}

	@Override
	public ZetaItem setCondition(BooleanSupplier enabledSupplier) {
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
