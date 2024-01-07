package org.violetmoon.zeta.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface ZEntityAttributeCreation extends IZetaLoadEvent {
	void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map);
}
