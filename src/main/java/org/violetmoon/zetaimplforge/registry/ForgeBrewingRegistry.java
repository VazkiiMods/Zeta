package org.violetmoon.zetaimplforge.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.registry.BrewingRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ForgeBrewingRegistry extends BrewingRegistry {

	private List<DelayedPotion> delayedPotions = new ArrayList<>();
	private boolean okToRegisterImmediately = false;

	public ForgeBrewingRegistry(ForgeZeta zeta) {
		super(zeta);
	}

    // Me when Im lazy and I dont wanna change that much code to fix this
    @EventBusSubscriber
	private record DelayedPotion(Holder<Potion> input, Item reagentSupplier, Holder<Potion> output) {
        private static Set<DelayedPotion> delayedPotions = new HashSet<>();

        void register() {
            delayedPotions.add(this);
        }

        @SubscribeEvent
		static void registryEventReal(RegisterBrewingRecipesEvent event) {
            PotionBrewing.Builder builder = event.getBuilder();
            for (DelayedPotion potion : delayedPotions)
                builder.addMix(potion.input, potion.reagentSupplier, potion.output);
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
