package org.violetmoon.zeta.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import java.util.function.Predicate;

public class SortedPredicatedKeyBinding extends SortedKeyBinding {
	private final Predicate<InputConstants.Key> allowed;

	public SortedPredicatedKeyBinding(String description, Type type, int keyCode, String category, int priority, Predicate<InputConstants.Key> allowed) {
		super(description, type, keyCode, category, priority);
		this.allowed = allowed;
	}

	//TODO ZETA: key modifiers were removed since they're a Forge extension

	@Override
	public void setKey(InputConstants.Key key) {
		if(allowed.test(key))
			super.setKey(key);
	}
}
