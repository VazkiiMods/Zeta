package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public interface ZRenderLiving extends IZetaPlayEvent {
	Entity getEntity();
	PoseStack getPoseStack();

	//ugly consequence of zeta's lackluster handling of event priorities, and me not wanting a combinatorial explosion
	interface PreHighest extends ZRenderLiving { }
	interface PostLowest extends ZRenderLiving { }
}
