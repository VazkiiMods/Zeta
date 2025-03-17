package org.violetmoon.zetaimplforge.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.violetmoon.zeta.registry.ZetaRegistry;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeZetaRegistry extends ZetaRegistry {

    // the keys of this are things like "minecraft:block", "minecraft:item" and so on
    ///TODO: WHY??
    private final Multimap<ResourceLocation, Supplier<Object>> defers = ArrayListMultimap.create();
    // to support calling getRegistryName before the object actually gets registered for real
    protected final Map<Object, ResourceLocation> internalNames = new IdentityHashMap<>();

    protected boolean completedReg = false;

    public ForgeZetaRegistry(ForgeZeta z) {
        super(z);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
    }

    //TODO: nuke
    @Override
    public <T> ResourceLocation getRegistryName(T obj, Registry<T> registry) {
        ResourceLocation internal = internalNames.get(obj);
        return internal == null ? registry.getKey(obj) : internal;
    }

    @Override
    protected <T> void odRegisterInternal(T obj, ResourceLocation id, ResourceKey<Registry<T>> registry) {
        if (completedReg) {
            throw new IllegalStateException("Attempted to register entries at an unusual time, after registration phase is done. Even tho the registry object is accessible here all the times (for now) you should only register things during the ZRegister event (or before?)");
        }
        internalNames.put(obj, id);
        defers.put(registry.location(), () -> obj);
    }

	//TODO: possibly nuke
    private void onRegisterEvent(RegisterEvent event) {
        var key = event.getRegistryKey();
        ResourceLocation registryRes = key.location();
        ResourceKey<Registry<Object>> keyGeneric = ResourceKey.createRegistryKey(registryRes);

        Collection<Supplier<Object>> ourEntries = defers.get(registryRes);
        if (!ourEntries.isEmpty()) {

            for (Supplier<Object> supplier : ourEntries) {
                Object entry = supplier.get();
                ResourceLocation name = internalNames.get(entry);
                z.log.debug("Registering to {} - {}", registryRes, name);
                event.register(keyGeneric, e -> e.register(name, entry));

                trackRegisteredObject(keyGeneric, event.getVanillaRegistry().wrapAsHolder(entry));
            }

            defers.removeAll(registryRes);
        }
    }

}
