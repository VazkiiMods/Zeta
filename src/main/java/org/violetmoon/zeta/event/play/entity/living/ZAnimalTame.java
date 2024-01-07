package org.violetmoon.zeta.event.play.entity.living;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public interface ZAnimalTame extends IZetaPlayEvent {
    Animal getAnimal();
    Player getTamer();
}
