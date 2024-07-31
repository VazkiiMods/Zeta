package org.violetmoon.zetaimplforge.api;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.api.IAdvancementModifier;
import org.violetmoon.zeta.api.IAdvancementModifierDelegate;
import org.violetmoon.zeta.event.load.ZGatherAdvancementModifiers;

import net.minecraftforge.eventbus.api.Event;

public class ForgeZGatherAdvancementModifiers extends Event implements ZGatherAdvancementModifiers {
	private final ZGatherAdvancementModifiers wrapped;

	public ForgeZGatherAdvancementModifiers(ZGatherAdvancementModifiers inner) {
		this.wrapped = inner;
	}

	@Override
	public void register(IAdvancementModifier modifier) {
		wrapped.register(modifier);
	}

	@Override
	public IAdvancementModifierDelegate getDelegate() {
		return wrapped.getDelegate();
	}

	//Note there are a ton of default methods available in ZGatherAdvancementModifiers for you to call.
}
