package org.violetmoon.zetaimplforge.event.play.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.event.play.entity.ZEntityInteract;

public record ForgeZEntityInteract(PlayerInteractEvent.EntityInteract e) implements ZEntityInteract {
	@Override
	public Entity getTarget() {
		return e.getTarget();
	}

	@Override
	public Player getEntity() {
		return e.getEntity();
	}

	@Override
	public Level getLevel() {
		return e.getLevel();
	}

	@Override
	public InteractionHand getHand() {
		return e.getHand();
	}

	@Override
	public ItemStack getItemStack() {
		return e.getItemStack();
	}
}
