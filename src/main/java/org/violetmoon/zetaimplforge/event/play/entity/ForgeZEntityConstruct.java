package org.violetmoon.zetaimplforge.event.play.entity;

import org.violetmoon.zeta.event.play.entity.ZEntityConstruct;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class ForgeZEntityConstruct implements ZEntityConstruct {
	private final EntityEvent.EntityConstructing e;

	public ForgeZEntityConstruct(EntityEvent.EntityConstructing e) {
		this.e = e;
	}

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}
}
