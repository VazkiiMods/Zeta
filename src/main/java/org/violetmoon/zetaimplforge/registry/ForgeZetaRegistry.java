package org.violetmoon.zetaimplforge.registry;

import java.util.Collection;
import java.util.function.Supplier;

import org.violetmoon.zeta.registry.ZetaRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.violetmoon.zetaimplforge.event.load.ForgeZRegister;

public class ForgeZetaRegistry extends ZetaRegistry {
	public ForgeZetaRegistry(ForgeZeta z) {
		super(z);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegister);
	}

	private boolean registered;

	public void onRegister(RegisterEvent e) {
		if (registered) {
			register(e);
		} else {
			registered = true; // RegisterEvent fires for each registry, to prevent multiple unwanted misfires do a 'registry done' check
			// that, or maybe its cause of both zeta and quark adding this as register event listener, idk

			z.loadBus.fire(new ForgeZRegister(z));
			z.loadBus.fire(new ForgeZRegister.Post());

			// firing those two events takes up a registry from the RegisterEvent
			register(e); // make up for that by calling our registry for said registry
		}
	}

	private void register(RegisterEvent event) {
		var key = event.getRegistryKey();
		ResourceLocation registryRes = key.location();
		ResourceKey<Registry<Object>> keyGeneric = ResourceKey.createRegistryKey(registryRes);

		Collection<Supplier<Object>> ourEntries = getDefers(registryRes);
		if(ourEntries != null && !ourEntries.isEmpty()) {

			for(Supplier<Object> supplier : ourEntries) {
				Object entry = supplier.get();
				ResourceLocation name = internalNames.get(entry);
                z.log.debug("Registering to {} - {}", registryRes, name);
				event.register(keyGeneric, e-> e.register(name, entry));

				trackRegisteredObject(keyGeneric, event.getVanillaRegistry().wrapAsHolder(entry));
			}

			clearDeferCache(registryRes);
		}
	}


}
