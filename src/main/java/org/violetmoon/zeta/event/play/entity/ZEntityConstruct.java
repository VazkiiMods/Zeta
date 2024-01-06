package org.violetmoon.zeta.event.play.entity;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.Entity;

public interface ZEntityConstruct extends IZetaPlayEvent {
	Entity getEntity();
}
