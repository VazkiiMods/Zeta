package org.violetmoon.zeta.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.item.ZetaDoubleHighBlockItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabHandler;
import org.violetmoon.zeta.registry.IZetaBlockItemProvider;
import org.violetmoon.zeta.registry.RenderLayerRegistry;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ZetaDoorBlock extends DoorBlock implements IZetaBlock, IZetaBlockItemProvider {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaDoorBlock(BlockSetType setType, String regname, @Nullable ZetaModule module, Properties properties) {
		super(properties, setType);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		Zeta zeta = module.zeta();
		zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		zeta.registry.registerBlock(this, regname, true);
		zeta.creativeTabs.addToCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, this);
		zeta.creativeTabs.addToCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS, this);
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
