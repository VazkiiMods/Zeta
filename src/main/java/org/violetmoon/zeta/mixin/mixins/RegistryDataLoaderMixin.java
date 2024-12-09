package org.violetmoon.zeta.mixin.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.zeta.util.RegisterDynamicUtil;

import com.mojang.serialization.Decoder;

import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;

// What's a little code registration between friends
@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

	@Inject(method = "loadContentsFromManager", at = @At("RETURN"))
	private static <E> void zeta$onLoadRegistryContents(ResourceManager manager, RegistryOps.RegistryInfoLookup lookup, WritableRegistry<E> registry, Decoder<E> whereWereGoingWeDontNeedParsers, Map<ResourceKey<?>, Exception> failed, CallbackInfo ci) {
		RegisterDynamicUtil.onRegisterDynamic(lookup, registry.key(), registry);
	}
}
