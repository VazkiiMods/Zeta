package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FabricZEntityInteract implements ZEntityInteract {
	private final Player player;
	private final Level level;
	private final InteractionHand hand;
	private final Entity entity;

	public FabricZEntityInteract(Player player, Level level, InteractionHand hand, Entity entity) {
		this.player = player;
		this.level = level;
		this.hand = hand;
		this.entity = entity;
	}

	@Override
	public Entity getTarget() {
		return entity;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public InteractionHand getHand() {
		return hand;
	}

	@Override
	public ItemStack getItemStack() {
		return getEntity().getItemInHand(hand);
	}
}
