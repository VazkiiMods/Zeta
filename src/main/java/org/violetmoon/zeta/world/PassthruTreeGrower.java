package org.violetmoon.zeta.world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

// All the vanilla TreeGrowers just hardcode ResourceKeys anyway.
public class PassthruTreeGrower extends AbstractTreeGrower {
	protected final ResourceKey<ConfiguredFeature<?, ?>> key;

	public PassthruTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> key) {
		this.key = key;
	}

	@Nullable
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) {
		return key;
	}

	public static class Mega extends AbstractMegaTreeGrower {
		protected final ResourceKey<ConfiguredFeature<?, ?>> key;
		protected final ResourceKey<ConfiguredFeature<?, ?>> megaKey; // 2x2

		public Mega(ResourceKey<ConfiguredFeature<?, ?>> key, ResourceKey<ConfiguredFeature<?, ?>> megaKey) {
			this.key = key;
			this.megaKey = megaKey;
		}

		@Nullable
		@Override
		protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_) {
			return key;
		}

		@Nullable
		@Override
		protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_222904_) {
			return megaKey;
		}
	}
}
