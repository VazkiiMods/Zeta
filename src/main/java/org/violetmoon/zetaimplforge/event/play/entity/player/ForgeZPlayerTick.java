package org.violetmoon.zetaimplforge.event.play.entity.player;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.violetmoon.zeta.event.play.entity.player.ZPlayerTick;

import net.minecraft.world.entity.player.Player;

public abstract class ForgeZPlayerTick implements ZPlayerTick {
    public final PlayerTickEvent e;

	protected ForgeZPlayerTick(PlayerTickEvent e) {
		this.e = e;
	}

	@Override
	public Player getPlayer() {
		return e.getEntity();
	}

	public static class Pre extends ForgeZPlayerTick implements ZPlayerTick.Start {
		public Pre(PlayerTickEvent e) {
			super(e);
		}
	}

	public static class Post extends ForgeZPlayerTick implements ZPlayerTick.End {
		public Post(PlayerTickEvent e) {
			super(e);
		}
	}
}
