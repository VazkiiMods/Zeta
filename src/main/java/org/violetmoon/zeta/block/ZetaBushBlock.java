package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.BushBlock;

public class ZetaBushBlock extends BushBlock implements IZetaBlock {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaBushBlock(String regname, @Nullable ZetaModule module, ResourceKey<CreativeModeTab> tab, Properties properties) {
		super(properties);
		this.module = module;

		if (module == null) //auto registration below this line
			return;

		Zeta zeta = module.zeta();
		zeta.registry.registerBlock(this, regname, true);
		zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		if (tab != null) {
			zeta.creativeTabs.addToCreativeTab(tab, this);
		}
	}

	@Override
	public ZetaBushBlock setCondition(BooleanSupplier enabledSupplier) {
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
