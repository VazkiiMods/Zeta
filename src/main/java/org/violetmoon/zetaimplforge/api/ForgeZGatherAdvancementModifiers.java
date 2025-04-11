package org.violetmoon.zetaimplforge.api;

import net.neoforged.bus.api.Event;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.api.IAdvancementModifier;
import org.violetmoon.zeta.api.IAdvancementModifierDelegate;
import org.violetmoon.zeta.event.load.ZGatherAdvancementModifiers;

public class ForgeZGatherAdvancementModifiers extends Event implements ZGatherAdvancementModifiers {
	private final ZGatherAdvancementModifiers inner;

	public ForgeZGatherAdvancementModifiers(Zeta zeta, ZGatherAdvancementModifiers inner) {
		this.inner = inner;
	}

	public ForgeZGatherAdvancementModifiers(ZGatherAdvancementModifiers inner) {
		this.inner = inner;
	}

	@Override
	public void register(IAdvancementModifier modifier) {
		inner.register(modifier);
	}

	@Override
	public IAdvancementModifierDelegate getDelegate() {
		return inner.getDelegate();
	}

	//Note there are a ton of default methods available in ZGatherAdvancementModifiers for you to call.
}
