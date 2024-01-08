package org.violetmoon.zeta.world.generator.multichunk;

import java.util.Random;
import java.util.function.BooleanSupplier;

import org.violetmoon.zeta.config.type.ClusterSizeConfig;
import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkGenerator;

public abstract class ClusterBasedGenerator extends MultiChunkFeatureGenerator {

	public final ClusterShape.Provider shapeProvider;

	public ClusterBasedGenerator(DimensionConfig dimConfig, ClusterSizeConfig sizeConfig, long seedXor) {
		this(dimConfig, BooleanSuppliers.TRUE, sizeConfig, seedXor);
	}

	public ClusterBasedGenerator(DimensionConfig dimConfig, BooleanSupplier condition, ClusterSizeConfig sizeConfig, long seedXor) {
		super(dimConfig, condition, seedXor);
		this.shapeProvider = new ClusterShape.Provider(sizeConfig, seedXor);
	}

	@Override
	public int getFeatureRadius() {
		return shapeProvider.getRadius();
	}

	@Override
	public void generateChunkPart(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world) {
		final ClusterShape shape = shapeProvider.around(src);
		final IGenerationContext context = createContext(src, generator, random, chunkCorner, world);

		forEachChunkBlock(world, chunkCorner, shape.getLowerBound(), shape.getUpperBound(), (pos) -> {
			if(context.canPlaceAt(pos) && shape.isInside(pos))
				context.consume(pos);
		});
	}

	public abstract IGenerationContext createContext(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world);

	public interface IGenerationContext {
		boolean canPlaceAt(BlockPos pos);
		void consume(BlockPos pos);
	}

}
