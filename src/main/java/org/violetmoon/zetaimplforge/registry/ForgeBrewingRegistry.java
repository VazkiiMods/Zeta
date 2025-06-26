package org.violetmoon.zetaimplforge.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.registry.BrewingRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.util.ArrayList;
import java.util.List;

public class ForgeBrewingRegistry extends BrewingRegistry {

	private List<DelayedPotion> delayedPotions = new ArrayList<>();
	private boolean okToRegisterImmediately = false;

	public ForgeBrewingRegistry(ForgeZeta zeta) {
		super(zeta);
	}

	private record DelayedPotion(Holder<Potion> input, Item reagentSupplier, Holder<Potion> output) {
		void register() {
			new PotionBrewing.Builder(FeatureFlagSet.of()).addMix(input, reagentSupplier, output);
		}
	}

	@Override
	public void addBrewingRecipe(Potion input, Item reagentSupplier, Potion output) {
		DelayedPotion d = new DelayedPotion(Holder.direct(input), reagentSupplier, Holder.direct(output));

		if(okToRegisterImmediately) {
			d.register();
		} else {
			delayedPotions.add(d);
		}
	}

	@LoadEvent
	public void commonSetup(ZCommonSetup event) {
		event.enqueueWork(() -> {
			okToRegisterImmediately = true;
			if (delayedPotions != null) delayedPotions.forEach(DelayedPotion::register);
			delayedPotions = null;
		});
	}
}
