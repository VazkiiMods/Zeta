package org.violetmoon.zeta.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.BushBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaBushBlock extends BushBlock implements IZetaBlock {
	public static final MapCodec<BushBlock> CODEC = simpleCodec(BushBlock::new);
	
	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaBushBlock(String regname, @Nullable ZetaModule module, ResourceKey<CreativeModeTab> tab, Properties properties) {
		super(properties);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta().registry.registerBlock(this, regname, true);
		module.zeta().renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		if(tab != null)
			setCreativeTab(tab);
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

	@Override
	public @NotNull MapCodec<BushBlock> codec() {
		return CODEC;
	}
}
