package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import org.violetmoon.zeta.item.ZetaDoubleHighBlockItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.registry.IZetaBlockItemProvider;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

public class ZetaDoorBlock extends DoorBlock implements IZetaBlock, IZetaBlockItemProvider {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaDoorBlock(BlockSetType setType, String regname, @Nullable ZetaModule module, Properties properties) {
		super(properties, setType);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		module.zeta.registry.registerBlock(this, regname, true);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, this);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS, this);
	}

	@Override
	public ZetaDoorBlock setCondition(BooleanSupplier enabledSupplier) {
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
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		return new ZetaDoubleHighBlockItem(this, props);
	}

}
