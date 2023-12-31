package org.violetmoon.zetaimplforge.client.event.load;

import org.violetmoon.zeta.client.event.load.ZAddModelLayers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;

public record ForgeZAddModelLayers(EntityRenderersEvent.AddLayers e) implements ZAddModelLayers {
	@Override
	public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType) {
		return e.getRenderer(entityType);
	}

	@Override
	public EntityModelSet getEntityModels() {
		return e.getEntityModels();
	}
}
