package org.violetmoon.zeta.client.event.play;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;

//NB. forge fires this in LocalPlayer.aiStep
public interface ZInputUpdate extends IZetaPlayEvent {
	Input getInput();
	Player getEntity();
}
