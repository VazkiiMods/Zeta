package org.violetmoon.zeta.world.generator.multichunk;

import java.util.Random;

import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.config.type.ClusterSizeConfig;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;

public record ClusterShape(BlockPos src, Vec3 radius,
		PerlinSimplexNoise noiseGenerator) {

	public boolean isInside(BlockPos pos) {
		// normalize distances by the radius
		double dx = (double) (pos.getX() - src.getX()) / radius.x;
		double dy = (double) (pos.getY() - src.getY()) / radius.y;
		double dz = (double) (pos.getZ() - src.getZ()) / radius.z;

		double r = dx * dx + dy * dy + dz * dz;
		if(r > 1)
			return false;
		if(ZetaGeneralConfig.useFastWorldgen)
			return true;

		r = Math.sqrt(r);

		// convert to spherical
		double phi = Math.atan2(dz, dx);
		double theta = r == 0 ? 0 : Math.acos(dy / r);

		// use phi, theta + the src pos to get noisemap uv
		double xn = phi + src.getX();
		double yn = theta + src.getZ();
		double noise = noiseGenerator.getValue(xn, yn, false);

		// when nearing the end of the loop, lerp back to the start to prevent it cutting off
		double cutoff = 0.75 * Math.PI;
		if(phi > cutoff) {
			double noise0 = noiseGenerator.getValue(-Math.PI + src.getX(), yn, false);
			noise = Mth.lerp((phi - cutoff) / (Math.PI - cutoff), noise, noise0);
		}

		// accept if within constrains
		double maxR = noise + 0.5;
		return (maxR - r) > 0;
	}

	public int getUpperBound() {
		return (int) Math.ceil(src.getY() + radius.y());
	}

	public int getLowerBound() {
		return (int) Math.floor(src.getY() - radius.y());
	}

	public static class Provider {

		private final ClusterSizeConfig config;
		private final PerlinSimplexNoise noiseGenerator;

		public Provider(ClusterSizeConfig config, long seed) {
			this.config = config;
			noiseGenerator = new PerlinSimplexNoise(new LegacyRandomSource(seed),
					ImmutableList.of(-4, -3, -2, -1, 0, 1, 2, 3, 4));
		}

		public ClusterShape around(BlockPos src) {
			Random rand = randAroundBlockPos(src);

			int radiusX = config.horizontalSize + rand.nextInt(config.horizontalVariation);
			int radiusY = config.verticalSize + rand.nextInt(config.verticalVariation);
			int radiusZ = config.horizontalSize + rand.nextInt(config.horizontalVariation);

			return new ClusterShape(src, new Vec3(radiusX, radiusY, radiusZ), noiseGenerator);
		}

		public int getRadius() {
			return config.horizontalSize + config.horizontalVariation;
		}

		public int getRarity() {
			return config.rarity;
		}

		public Random randAroundBlockPos(BlockPos pos) {
			return new Random(31 * (31L * (31 + pos.getX()) + pos.getY()) + pos.getZ());
		}

	}

}
