package org.violetmoon.zetaimplforge.client.event.load;

import org.violetmoon.zeta.client.event.load.ZKeyMapping;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public record ForgeZKeyMapping(RegisterKeyMappingsEvent e) implements ZKeyMapping {
	@Override
	public KeyMapping register(KeyMapping key) {
		e.register(key);
		return key;
	}
}
