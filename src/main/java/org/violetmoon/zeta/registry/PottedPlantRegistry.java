package org.violetmoon.zeta.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface PottedPlantRegistry {
	//Don't ask me what ResourceLocation flower; is for, it's a weird forge api tbh
	void addPot(ResourceLocation flower, Block fullPot);
}
