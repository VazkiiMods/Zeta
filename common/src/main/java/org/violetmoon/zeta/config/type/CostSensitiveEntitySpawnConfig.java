package org.violetmoon.zeta.config.type;

import org.violetmoon.zeta.config.Config;

public class CostSensitiveEntitySpawnConfig extends EntitySpawnConfig {

	@Config
	public double maxCost;

	@Config
	public double spawnCost;

	public CostSensitiveEntitySpawnConfig(int spawnWeight, int minGroupSize, int maxGroupSize, double maxCost, double spawnCost, IBiomeConfig biomes) {
		super(spawnWeight, minGroupSize, maxGroupSize, biomes);
		this.maxCost = maxCost;
		this.spawnCost = spawnCost;
	}

}
