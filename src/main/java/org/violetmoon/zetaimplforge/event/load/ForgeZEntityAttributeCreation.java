package org.violetmoon.zetaimplforge.event.load;

import org.violetmoon.zeta.event.load.ZEntityAttributeCreation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

public record ForgeZEntityAttributeCreation(EntityAttributeCreationEvent e) implements ZEntityAttributeCreation {

	@Override
	public void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map) {
		e.put(entity, map);
	}
}
