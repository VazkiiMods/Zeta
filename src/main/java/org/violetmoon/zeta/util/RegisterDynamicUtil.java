package org.violetmoon.zeta.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.violetmoon.zeta.Zeta;

/**
 * Weird supporting code for RegistryDataLoaderMixin. Mixing into RegistryDataLoader
 * is inherently a global thing, but Zeta is a non-static library where there might be several instances of it
 * in a running game, so *some* static needs to be involved in order to connect the two.
 *
 * TODO: Would it be better to just... have a global list of Zeta instances, that can be queried for situations like this?
 *
 * @see org.violetmoon.zeta.mixin.mixins.RegistryDataLoaderMixin for implementation
 */
public class RegisterDynamicUtil {

	private static final Set<Zeta> interestedParties = new HashSet<>(2);

	public static void signup(Zeta z) {
		interestedParties.add(z);
	}

	public static <E> void onRegisterDynamic(RegistryOps.RegistryInfoLookup lookup, ResourceKey<? extends Registry<E>> registryId, WritableRegistry<E> registry) {
		interestedParties.forEach(z -> z.registry.performDynamicRegistration(lookup, registryId, registry));
	}

}
