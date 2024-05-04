package org.violetmoon.zeta.event.play.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public abstract class FabricZPlayerTick implements ZPlayerTick {
	private final TickEvent.PlayerTickEvent e;

	protected FabricZPlayerTick(TickEvent.PlayerTickEvent e) {
		this.e = e;
	}

	@Override
	public Player getPlayer() {
		return e.player;
	}

	public static class Start extends FabricZPlayerTick implements ZPlayerTick.Start {
		public Start(TickEvent.PlayerTickEvent e) {
			super(e);
		}
	}

	public static class End extends FabricZPlayerTick implements ZPlayerTick.End {
		public End(TickEvent.PlayerTickEvent e) {
			super(e);
		}
	}
}
