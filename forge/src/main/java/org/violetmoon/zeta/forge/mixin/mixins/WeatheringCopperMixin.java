package org.violetmoon.zeta.forge.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.zeta.annotation.DelegateInterfaceMixin;
import org.violetmoon.zeta.mixin.plugin.DelegateReturnValueModifier;
import org.violetmoon.zeta.forge.mixin.delegate.WeatheringCopperDelegate;

import net.minecraft.world.level.block.WeatheringCopper;

@Mixin(WeatheringCopper.class)
@DelegateInterfaceMixin(delegate = WeatheringCopperDelegate.class, methods = {
	@DelegateReturnValueModifier(target = "getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;",
		delegate = "customWeatheringPrevious", desc = "(Ljava/util/Optional;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"),
	@DelegateReturnValueModifier(target = "getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Ljnet/minecraft/world/level/block/state/BlockState;",
		delegate = "customWeatheringFirst", desc = "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"),

	// Refmapping isn't a thing for this cursed hell.
	@DelegateReturnValueModifier(target = "m_154899_(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;",
		delegate = "customWeatheringPrevious", desc = "(Ljava/util/Optional;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"),
	@DelegateReturnValueModifier(target = "m_154906_(Lnet/minecraft/world/level/block/state/BlockState;)Ljnet/minecraft/world/level/block/state/BlockState;",
		delegate = "customWeatheringFirst", desc = "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
})
public interface WeatheringCopperMixin {
	// Delegated. Only valid because WeatheringCopper members are not refmapped.

	//TODO ZETA: use a real interface mixin on Fabric
}
