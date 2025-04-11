package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.play.ZItemTooltip;

import java.util.List;

public record ForgeZItemTooltip(ItemTooltipEvent e) implements ZItemTooltip {

	@Override
	public TooltipFlag getFlags() {return e.getFlags();}

	@Override
	public @NotNull ItemStack getItemStack() {return e.getItemStack();}

	@Override
	public List<Component> getToolTip() {return e.getToolTip();}

	@Override
	public @Nullable Player getEntity() {return e.getEntity();}
}
