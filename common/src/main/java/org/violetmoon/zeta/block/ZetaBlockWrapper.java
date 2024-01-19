package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

// Wrapper to allow vanilla blocks to be treated as zeta blocks contextualized under a module
public class ZetaBlockWrapper implements IZetaBlock, ItemLike {

	private final Block parent;
	private final @Nullable ZetaModule module;
	
	private BooleanSupplier condition;
	
	public ZetaBlockWrapper(Block parent, @Nullable ZetaModule module) {
		this.parent = parent;
		this.module = module;
	}
	
	@Override
	public Block getBlock() {
		return parent;
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public ZetaBlockWrapper setCondition(BooleanSupplier condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return condition == null || condition.getAsBoolean();
	}

	@Override
	public Item asItem() {
		return parent.asItem();
	}

}
