package org.violetmoon.zeta.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.violetmoon.zeta.block.ext.IZetaBlockExtensions;
import org.violetmoon.zeta.module.IDisableable;
import org.violetmoon.zeta.registry.CreativeTabManager;

/**
 * @author WireSegal
 * Created at 1:14 PM on 9/19/19.
 */
public interface IZetaBlock extends IZetaBlockExtensions, IDisableable<IZetaBlock> {

	default Block getBlock() {
		return (Block) this;
	}

    default Block setCreativeTab(ResourceKey<CreativeModeTab> tab) {
    	Block b = getBlock();
    	CreativeTabManager.addToCreativeTab(tab, b);
    	return b;
    }
    
    default Block setCreativeTab(ResourceKey<CreativeModeTab> tab, ItemLike parent, boolean behindParent) {
    	Block b = getBlock();
    	CreativeTabManager.addToCreativeTabNextTo(tab, b, parent, behindParent);
    	
    	return b;
    }
	
	@Override
	default int getFlammabilityZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		//TODO 1.20: weird

		SoundType totallyMaterialTrustMeImADolphin = state.getSoundType();
		if(totallyMaterialTrustMeImADolphin == SoundType.WOOL)
			return 60;
		if(totallyMaterialTrustMeImADolphin == SoundType.WOOD || state.ignitedByLava()) //i dont know what im doing
			return 20;
		ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		if (loc != null && (loc.getPath().endsWith("_log") || loc.getPath().endsWith("_wood")))
			return 5;
		else
			return 0;
	}

	@Override
	default int getFireSpreadSpeedZeta(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
			return 0;

		//TODO 1.20: weird
		SoundType gaming = state.getSoundType();
		if(gaming == SoundType.WOOL) //or leaves
			return 30;
		if(gaming == SoundType.WOOD || state.ignitedByLava())
			return 5;
		else
			return 0;
	}

}
