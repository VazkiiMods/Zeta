package org.violetmoon.zeta.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.item.ZetaBlockItem;
import org.violetmoon.zeta.util.RegisterDynamicUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

//Mash of arl's RegistryHelper and its ModData innerclass.
//You're expected to create one of these per modid instead, avoiding a dependency on Forge's "current mod id" notion.
public abstract class ZetaRegistry {
	protected final Zeta z;

	// the keys of this are things like "minecraft:block", "minecraft:item" and so on
	private final Multimap<ResourceLocation, Supplier<Object>> defers = ArrayListMultimap.create();
	
	// to support calling getRegistryName before the object actually gets registered for real
	protected final Map<Object, ResourceLocation> internalNames = new IdentityHashMap<>();
	
	// "named color provider" system allows blocks and items to choose their own color providers in a side-safe way
	// TODO: should this go somewhere else and not be so tightly-integrated? (yes - i think a Registrate-like system would be a great spot for this)
	private final Map<Block, String> blocksToColorProviderName = new HashMap<>();
	private final Map<Item, String> itemsToColorProviderName = new HashMap<>();

	// Hastily tacked-on dynamic registry bullshit. RegistryInfoLookup allows reading from other dynamic registries while creating your object... I think?
	private record DynamicEntry<T>(ResourceKey<T> id, Function<RegistryOps.RegistryInfoLookup, T> creator, @Nullable LateBoundHolder<T> lateBound) { }
	private final Map<ResourceKey<Registry<?>>, List<DynamicEntry<?>>> dynamicDefers = new HashMap<>();

	public ZetaRegistry(Zeta z) {
		this.z = z;
	}

	public <T> ResourceLocation getRegistryName(T obj, Registry<T> registry) {
		ResourceLocation internal = internalNames.get(obj);
		return internal == null ? registry.getKey(obj) : internal;
	}

	//You know how `new ResourceLocation(String)` prepends "minecraft" if there's no prefix?
	//This method is like that, except it prepends *your* modid
	public ResourceLocation newResourceLocation(String in) {
		if(in.indexOf(':') == -1) return new ResourceLocation(z.modid, in);
		else return new ResourceLocation(in);
	}

	//Root registration method
	public <T> void register(T obj, ResourceLocation id, ResourceKey<Registry<T>> registry) {
		if(obj == null)
			throw new IllegalArgumentException("Can't register null object.");

		if(obj instanceof Block block && obj instanceof IZetaBlockColorProvider provider && provider.getBlockColorProviderName() != null)
			blocksToColorProviderName.put(block, provider.getBlockColorProviderName());

		if(obj instanceof Item item && obj instanceof IZetaItemColorProvider provider && provider.getItemColorProviderName() != null)
			itemsToColorProviderName.put(item, provider.getItemColorProviderName());

		internalNames.put(obj, id);
		defers.put(registry.location(), () -> obj);
	}

	public <T> void register(T obj, String resloc, ResourceKey<Registry<T>> registry) {
		register(obj, newResourceLocation(resloc), registry);
	}

	public void registerItem(Item item, ResourceLocation id) {
		register(item, id, Registries.ITEM);
	}

	public void registerItem(Item item, String resloc) {
		register(item, newResourceLocation(resloc), Registries.ITEM);
	}

	public void registerBlock(Block block, ResourceLocation id, boolean hasBlockItem) {
		register(block, id, Registries.BLOCK);

		if(hasBlockItem)
			defers.put(Registries.ITEM.location(), () -> createItemBlock(block));
	}

	public void registerBlock(Block block, String resloc, boolean hasBlockItem) {
		registerBlock(block, newResourceLocation(resloc), hasBlockItem);
	}

	public void registerBlock(Block block, ResourceLocation id) {
		registerBlock(block, id, true);
	}

	public void registerBlock(Block block, String resloc) {
		registerBlock(block, resloc, true);
	}

	private Item createItemBlock(Block block) {
		Item.Properties props = new Item.Properties();
		ResourceLocation registryName = internalNames.get(block);

		if(block instanceof IZetaItemPropertiesFiller filler)
			filler.fillItemProperties(props);

		BlockItem blockitem;
		if(block instanceof IZetaBlockItemProvider)
			blockitem = ((IZetaBlockItemProvider) block).provideItemBlock(block, props);
		else blockitem = new ZetaBlockItem(block, props);

		if(block instanceof IZetaItemColorProvider prov && prov.getItemColorProviderName() != null)
			itemsToColorProviderName.put(blockitem, prov.getItemColorProviderName());

		internalNames.put(blockitem, registryName);
		return blockitem;
	}

	/// performing registration (regular startup entries) ///

	public Collection<Supplier<Object>> getDefers(ResourceLocation registryId) {
		return defers.get(registryId);
	}

	public void clearDeferCache(ResourceLocation resourceLocation) {
		defers.removeAll(resourceLocation);
	}

	public void finalizeBlockColors(BiConsumer<Block, String> consumer) {
		blocksToColorProviderName.forEach(consumer);
		blocksToColorProviderName.clear();
	}

	public void finalizeItemColors(BiConsumer<Item, String> consumer) {
		itemsToColorProviderName.forEach(consumer);
		itemsToColorProviderName.clear();
	}

	/// performing registration (dynamic registry jank - for registering ConfiguredFeature etc through code) ///
	/// check out the vanilla RegistryDataLoader.WORLDGEN_REGISTRIES for a list of registries this works on. ///

	// Some explanation:
	// - BuiltInRegistries are "BuiltIn" because their contents are baked into the game (as opposed to being loaded through datapacks.)
	//    You can access them from everywhere.
	// - Because the worldgen registries are datapackable, their contents depend on the MinecraftServer. So they are not accessible
	//    everywhere. You need a live MinecraftServer to read from worldgen registries.[^1]
	// - This API allows you to register stuff to these registries with an "easy" API. Much like how "real" block registration is deferred
	//    until the right time, RegisterDynamicUtil/RegistryDataLoaderMixin take care of actually registering these to the game at the right time.
	// - The "Holder" type represents a handle into a registry. You can obtain the live object with "get()", but to avoid hairy registry-ordering
	//    problems, some Minecraft APIs accept the Holder instead. This allows you to *mention* things before they're actually registered.
	// - The "HolderGetter" type is a stripped-down interface to Registry that allows you to obtain Holders from it.
	// - The "RegistryInfoLookup" type allows you to obtain HolderGetters for whatever worldgen registry you want.
	//    This object is supplied by vanilla. If you'd like to grab one, use the lazy registration api (registerDynamicF)
	//
	// [^1] This is what the RegistryAccess type is for. You can compare it to RegistryInfoLookup, but it's more suited to obtaining stuff
	//  after the complete set of registered objects is known, i.e. no Holder funny business is necessary.

	// just some java bs dont worry about it
	@SuppressWarnings({
		"unchecked",
		"RedundantCast" // intellij static analysis bug? huh? lol
	})
	private <T> ResourceKey<Registry<?>> erase(ResourceKey<? extends Registry<T>> weeeejava) {
		return (ResourceKey<Registry<?>>) (Object) weeeejava;
	}

	/**
	 * Register something to a worldgen registry that requires a `RegistryInfoLookup` to construct.
	 * You might use this when constructing placed features (which require configuredfeatures), biomes (which require placedfeatures and configuredcarvers), etc.
	 *
	 * The returned Holder type is a little odd.
	 *  - We can't return a Holder.Direct, since it's not possible to construct the object yet.
	 *  - We can't return a Holder.Reference, since the registry we're adding the object *to* doesn't even exist yet.
	 */
	public <T> LateBoundHolder<T> registerDynamicF(Function<RegistryOps.RegistryInfoLookup, T> objCreator, ResourceKey<T> id, ResourceKey<? extends Registry<T>> registry) {
		RegisterDynamicUtil.signup(z);

		LateBoundHolder<T> hell = new LateBoundHolder<>(id); //umm

		dynamicDefers.computeIfAbsent(erase(registry), __ -> new ArrayList<>()).add(new DynamicEntry<>(id, objCreator, hell));
		return hell;
	}

	public <T> LateBoundHolder<T> registerDynamicF(Function<RegistryOps.RegistryInfoLookup, T> objCreator, ResourceLocation id, ResourceKey<? extends Registry<T>> registry) {
		return registerDynamicF(objCreator, ResourceKey.create(registry, id), registry);
	}

	public <T> LateBoundHolder<T> registerDynamicF(Function<RegistryOps.RegistryInfoLookup, T> objCreator, String regname, ResourceKey<? extends Registry<T>> registry) {
		return registerDynamicF(objCreator, newResourceLocation(regname), registry);
	}

	/**
	 * Register something to a worldgen registry that can be immediately constructed.
	 */
	@Deprecated(forRemoval = true)
	public <T> Holder.Direct<T> registerDynamic(T obj, ResourceKey<T> id, ResourceKey<? extends Registry<T>> registry) {
		RegisterDynamicUtil.signup(z);
		dynamicDefers.computeIfAbsent(erase(registry), __ -> new ArrayList<>()).add(new DynamicEntry<>(id, __ -> obj, null));

		return new Holder.Direct<>(obj);
	}

	@Deprecated(forRemoval = true)
	public <T> Holder.Direct<T> registerDynamic(T obj, ResourceLocation id, ResourceKey<? extends Registry<T>> registry) {
		return registerDynamic(obj, ResourceKey.create(registry, id), registry);
	}

	public <T> Holder.Direct<T> registerDynamic(T obj, String regname, ResourceKey<? extends Registry<T>> registry) {
		return registerDynamic(obj, newResourceLocation(regname), registry);
	}

	@SuppressWarnings("unchecked")
	public <T> void performDynamicRegistration(RegistryOps.RegistryInfoLookup lookup, ResourceKey<? extends Registry<?>> registryKey, WritableRegistry<T> writable) {
		List<DynamicEntry<?>> entries = dynamicDefers.get(registryKey);
		if(entries == null || entries.isEmpty())
			return;

		z.log.info("Dynamically registering {} thing{} into {}", entries.size(), entries.size() > 1 ? "s" : "", registryKey.location());

		List<DynamicEntry<T>> typePun = ((List<DynamicEntry<T>>) (Object) entries);
		typePun.forEach(entry -> {
			T thing = entry.creator.apply(lookup);
			writable.register(entry.id, thing, Lifecycle.stable());

			if(entry.lateBound != null)
				entry.lateBound.bind(thing, writable);
		});
	}
}
