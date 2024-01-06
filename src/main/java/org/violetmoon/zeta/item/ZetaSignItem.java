package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaSignItem extends SignItem implements IZetaItem {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaSignItem(@Nullable ZetaModule module, Block sign, Block wallSign) {
		super(new Item.Properties().stacksTo(16), sign, wallSign);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		String resloc = module.zeta.registryUtil.inherit(sign, "%s");
		module.zeta.registry.registerItem(this, resloc);
		setCreativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, Blocks.CHEST, true);
	}

	@Override
	public ZetaSignItem setCondition(BooleanSupplier enabledSupplier) {
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
