package org.violetmoon.zeta.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.mixin.mixins.AccessorPotionBrewing;
import org.violetmoon.zeta.registry.BrewingRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeBrewingRegistry extends BrewingRegistry {
	public ForgeBrewingRegistry(Zeta zeta) {
		super(zeta);
	}

	private record DelayedPotion(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
		void register() {
			setupRegister(input,reagentSupplier,output);
		}
	}
	private List<DelayedPotion> delayedPotions = new ArrayList<>();
	private boolean okToRegisterImmediately = false;

	@Override
	public void addBrewingRecipe(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
		DelayedPotion d = new DelayedPotion(input, reagentSupplier, output);

		if(okToRegisterImmediately)
			d.register();
		else
			delayedPotions.add(d);
	}

	@LoadEvent
	public void commonSetup(ZCommonSetup event) {
		event.enqueueWork(() -> {
			okToRegisterImmediately = true;
			delayedPotions.forEach(DelayedPotion::register);
			delayedPotions = null;
		});
	}

	@ExpectPlatform
	public static void setupRegister(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
		throw new AssertionError();
	}
}
