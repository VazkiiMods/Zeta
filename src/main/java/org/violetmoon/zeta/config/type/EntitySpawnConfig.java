package org.violetmoon.zeta.config.type;

import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.module.ZetaModule;

public class EntitySpawnConfig implements IConfigType {

	private boolean enabled = false;

	@Config
	@Config.Min(value = 0, exclusive = true)
	public int spawnWeight;

	@Config
	@Config.Min(1)
	public int minGroupSize;

	@Config
	@Config.Min(1)
	public int maxGroupSize;

	@Config
	public IBiomeConfig biomes;

	public EntitySpawnConfig(int spawnWeight, int minGroupSize, int maxGroupSize, IBiomeConfig biomes) {
		this.spawnWeight = spawnWeight;
		this.minGroupSize = minGroupSize;
		this.maxGroupSize = maxGroupSize;
		this.biomes = biomes;
	}

	@Override
	public void onReload(ZetaModule module, ConfigFlagManager flagManager) {
		enabled = (module != null && module.isEnabled());
	}

	public boolean isEnabled() {
		return enabled;
	}

}
