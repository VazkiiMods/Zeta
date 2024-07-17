package org.violetmoon.zeta.event.play.entity.living;

import java.util.Collection;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.helpers.LivingGetter;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;

public interface ZLivingDrops extends IZetaPlayEvent, Cancellable, LivingGetter {
	DamageSource getSource();
	Collection<ItemEntity> getDrops();
	boolean isRecentlyHit();

	interface Lowest extends ZLivingDrops {}
}
