package org.violetmoon.zeta.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;

import java.util.function.Predicate;

public class PredicatedKeyBinding extends KeyMapping {
	private final Predicate<InputConstants.Key> allowed;

	public PredicatedKeyBinding(String description, Type type, int keyCode, String category, Predicate<InputConstants.Key> allowed) {
		super(description, type, keyCode, category);
		this.allowed = allowed;
	}

	//TODO ZETA: key modifiers were removed since they're a Forge extension

	@Override
	public void setKey(InputConstants.Key key) {
		if(allowed.test(key))
			super.setKey(key);
	}
}
