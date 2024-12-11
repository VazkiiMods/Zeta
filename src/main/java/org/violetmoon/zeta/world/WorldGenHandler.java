package org.violetmoon.zeta.world;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.world.generator.Generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenHandler {

	public static final Map<GenerationStep.Decoration, Holder<PlacedFeature>> defers = new EnumMap<>(GenerationStep.Decoration.class);
	public static final Map<GenerationStep.Decoration, SortedSet<WeightedGenerator>> generators = new EnumMap<>(GenerationStep.Decoration.class);

	@LoadEvent
	public static void register(ZRegister event) {
		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			Feature<NoneFeatureConfiguration> deferredFeature = new DeferredFeature(stage);

			String name = "deferred_feature_" + stage.name().toLowerCase(Locale.ROOT);
			ZetaMod.ZETA.registry.register(deferredFeature, name, Registries.FEATURE);

			ConfiguredFeature<?, ?> feature = new ConfiguredFeature<>(deferredFeature, FeatureConfiguration.NONE);

			Holder<ConfiguredFeature<?, ?>> featureHolder = Holder.direct(feature);

			PlacedFeature placed = new PlacedFeature(featureHolder, List.of());
			Holder<PlacedFeature> placedHolder = Holder.direct(placed);

			defers.put(stage, placedHolder);
		}
	}

	public static void addGenerator(ZetaModule module, Generator generator, GenerationStep.Decoration stage, int weight) {
		WeightedGenerator weighted = new WeightedGenerator(module, generator, weight);
		if(!generators.containsKey(stage))
			generators.put(stage, new TreeSet<>());

		generators.get(stage).add(weighted);
	}

	public static void generateChunk(FeaturePlaceContext<NoneFeatureConfiguration> context, GenerationStep.Decoration stage) {
		WorldGenLevel level = context.level();
		if(!(level instanceof WorldGenRegion region))
			return;

		ChunkGenerator generator = context.chunkGenerator();
		BlockPos origin = context.origin();
		BlockPos pos = new BlockPos(origin.getX(), 0, origin.getZ());
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(region.getSeed()));
		ChunkPos center = region.getCenter();
		long seed = random.setDecorationSeed(region.getSeed(), center.x * 16, center.z * 16);
		int stageNum = stage.ordinal() * 10000;

		if(generators.containsKey(stage)) {
			SortedSet<WeightedGenerator> set = generators.get(stage);

			for(WeightedGenerator wgen : set) {
				Generator gen = wgen.generator();

				if(wgen.module().enabled && gen.canGenerate(region)) {

					/* CONFIG FLAG
					if (ZetaGeneralConfig.enableWorldgenWatchdog) {
						final int finalStageNum = stageNum;
						stageNum = watchdogRun(gen, () -> gen.generate(finalStageNum, seed, stage, region, generator, random, pos), 1, TimeUnit.MINUTES);
					} else
					 */
					stageNum = gen.generate(stageNum, seed, stage, region, generator, random, pos);
				}
			}
		}
	}

	private static int watchdogRun(Generator gen, Callable<Integer> run, int time, TimeUnit unit) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Integer> future = exec.submit(run);
		exec.shutdown();

		try {
			return future.get(time, unit);
		} catch (Exception e) {
			throw new RuntimeException("Error generating " + gen, e);
		}
	}

}
