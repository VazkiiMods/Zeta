package org.violetmoon.zeta.event.play;

import java.util.List;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public interface ZItemTooltip extends IZetaPlayEvent {
	TooltipFlag getFlags();
	ItemStack getItemStack();
	List<Component> getToolTip();
	Player getEntity();
}
