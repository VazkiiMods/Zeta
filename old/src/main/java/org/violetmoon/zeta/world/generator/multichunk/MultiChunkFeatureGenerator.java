package org.violetmoon.zeta.world.generator.multichunk;

import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.violetmoon.zeta.config.type.DimensionConfig;
import org.violetmoon.zeta.world.generator.Generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkGenerator;

public abstract class MultiChunkFeatureGenerator extends Generator {

	private final long seedXor;

	public MultiChunkFeatureGenerator(DimensionConfig dimConfig, BooleanSupplier condition, long seedXor) {
		super(dimConfig, condition);
		this.seedXor = seedXor;
	}

	@Override
	public final void generateChunk(WorldGenRegion world, ChunkGenerator generator, RandomSource rand, BlockPos pos) {
		int radius = getFeatureRadius();
		if(radius <= 0)
			return;

		int chunkRadius = (int) Math.ceil(radius / 16.0);

		long worldSeed = world.getSeed();
		Random worldRandom = new Random(worldSeed);
		long xSeed = worldRandom.nextLong();
		long zSeed = worldRandom.nextLong();

		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed ^ seedXor;
		Random ourRandom = new Random(chunkSeed);

		for(int x = chunkX - chunkRadius; x <= chunkX + chunkRadius; x++)
			for(int z = chunkZ - chunkRadius; z <= chunkZ + chunkRadius; z++) {
				chunkSeed = (xSeed * x + zSeed * z) ^ worldSeed ^ seedXor;
				Random chunkRandom = new Random(chunkSeed);
				BlockPos chunkCorner = new BlockPos(x << 4, 0, z << 4);

				for(BlockPos source : getSourcesInChunk(world, chunkRandom, generator, chunkCorner))
					generateChunkPart(source, generator, ourRandom, pos, world);
			}
	}

	public abstract int getFeatureRadius();

	public abstract void generateChunkPart(BlockPos src, ChunkGenerator generator, Random random, BlockPos chunkCorner, WorldGenRegion world);

	public abstract BlockPos[] getSourcesInChunk(WorldGenRegion world, Random random, ChunkGenerator generator, BlockPos chunkLeft);

	public void forEachChunkBlock(LevelReader level, BlockPos chunkCorner, int minY, int maxY, Consumer<BlockPos> func) {
		minY = Math.max(level.getMinBuildHeight() + 1, minY);
		maxY = Math.min(level.getMaxBuildHeight() - 1, maxY);
		int chunkCornerX = chunkCorner.getX(); //hoisting out of loop
		int chunkCornerZ = chunkCorner.getZ();

		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(0, 0, 0);
		for(int x = 0; x < 16; x++)
			for(int y = minY; y < maxY; y++)
				for(int z = 0; z < 16; z++) {
					mutable.set(chunkCornerX + x, y, chunkCornerZ + z);
					func.accept(mutable);
				}
	}

	public boolean isInsideChunk(BlockPos pos, int chunkX, int chunkZ) {
		int x = chunkX * 16;
		int z = chunkZ * 16;
		return pos.getX() > x && pos.getZ() > z && pos.getX() < (x + 16) && pos.getZ() < (z + 16);
	}

}
