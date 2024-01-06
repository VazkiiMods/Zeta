package org.violetmoon.zeta.client.event.load;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public interface ZAddModelLayers extends IZetaLoadEvent {
	<T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType);
	EntityModelSet getEntityModels();
}
