package org.violetmoon.zeta.config.type;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeConfig {

	boolean canSpawn(Holder<Biome> b);

}
