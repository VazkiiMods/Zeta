package org.violetmoon.zetaimplforge.api;

import net.minecraftforge.eventbus.api.Event;

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

	public Zeta getZeta() {
		return zeta;
	}

	public String getModid() {
		return zeta.modid;
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
