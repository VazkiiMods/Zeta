package org.violetmoon.zeta.registry;

import org.violetmoon.zeta.FabricZeta;

public class FabricZetaRegistry extends ZetaRegistry {
	public FabricZetaRegistry(FabricZeta z) {
		super(z);
	}

	private void onRegisterEvent() {
		/*ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
		ResourceLocation registryRes = key.location();
		ResourceKey<Registry<Object>> keyGeneric = ResourceKey.createRegistryKey(registryRes);

		Collection<Supplier<Object>> ourEntries = getDefers(registryRes);
		if(ourEntries != null && !ourEntries.isEmpty()) {

			for(Supplier<Object> supplier : ourEntries) {
				Object entry = supplier.get();
				ResourceLocation name = internalNames.get(entry);
				z.log.debug("Registering to " + registryRes + " - " + name);
				event.register(keyGeneric, e-> e.register(name, entry));
			}

			clearDeferCache(registryRes);
		}*/
	}
}
