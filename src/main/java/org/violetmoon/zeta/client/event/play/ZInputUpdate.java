package org.violetmoon.zeta.client.event.play;

import net.minecraft.client.player.ClientInput;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.player.Player;

//NB. forge fires this in LocalPlayer.aiStep
public interface ZInputUpdate extends IZetaPlayEvent {
	ClientInput getInput();
	Player getEntity();
}
