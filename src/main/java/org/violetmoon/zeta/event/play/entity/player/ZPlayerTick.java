package org.violetmoon.zeta.event.play.entity.player;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.player.Player;

public interface ZPlayerTick extends IZetaPlayEvent {
	Player getPlayer();

	//extracting forge's event phases
	interface Start extends ZPlayerTick { }
	interface End extends ZPlayerTick { }
}
