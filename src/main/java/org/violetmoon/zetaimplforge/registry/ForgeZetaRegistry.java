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

public class ForgeZetaRegistry extends ZetaRegistry {
	public ForgeZetaRegistry(ForgeZeta z) {
		super(z);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
	}

	private void onRegisterEvent(RegisterEvent event) {
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
