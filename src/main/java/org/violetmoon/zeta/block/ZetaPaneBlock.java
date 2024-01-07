package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry.Layer;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaPaneBlock extends IronBarsBlock implements IZetaBlock {

	public final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaPaneBlock(String name, @Nullable ZetaModule module, Block.Properties properties, Layer renderLayer) {
		super(properties);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerBlock(this, name, true);
		if(renderLayer != null)
			module.zeta.renderLayerRegistry.put(this, renderLayer);
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public ZetaPaneBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}


}
