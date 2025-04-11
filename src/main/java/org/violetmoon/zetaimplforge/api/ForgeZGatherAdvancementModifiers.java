package org.violetmoon.zetaimplforge.api;

import net.neoforged.bus.api.Event;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.api.IAdvancementModifier;
import org.violetmoon.zeta.api.IAdvancementModifierDelegate;
import org.violetmoon.zeta.event.load.ZGatherAdvancementModifiers;

public class GatherAdvancementModifiersEvent extends Event implements ZGatherAdvancementModifiers {
	private final Zeta zeta;
	private final ZGatherAdvancementModifiers inner;

	public GatherAdvancementModifiersEvent(Zeta zeta, ZGatherAdvancementModifiers inner) {
		this.zeta = zeta;
		this.inner = inner;
	}

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
