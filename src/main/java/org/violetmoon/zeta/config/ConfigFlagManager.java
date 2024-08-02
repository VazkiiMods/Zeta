package org.violetmoon.zeta.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.recipe.FlagIngredient;
import org.violetmoon.zeta.registry.CraftingExtensionsRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class ConfigFlagManager {

	public final Zeta zeta;

	private final Set<String> allFlags = new HashSet<>();
	private final Map<String, Boolean> flags = new HashMap<>();

	//TODO augh; needed for BrewingRegistry
	public final FlagIngredient.Serializer flagIngredientSerializer = new FlagIngredient.Serializer(this);

	public static final LootItemConditionType FLAG_CONDITION_TYPE = new LootItemConditionType(FlagLootCondition.CODEC);

	public ConfigFlagManager(Zeta zeta) {
		this.zeta = zeta;

		zeta.loadBus.subscribe(this);
	}

	@LoadEvent
	public void onRegister(ZRegister event) {
		CraftingExtensionsRegistry ext = event.getCraftingExtensionsRegistry();

		//Note: These SHOULD be Quark-independent already, but hell if I know. Todo: Double check it.
		ext.registerConditionSerializer(new FlagCondition.Serializer(this, ResourceLocation.fromNamespaceAndPath(zeta.modid, "flag")));
		ext.registerConditionSerializer(new FlagCondition.Serializer(this, ResourceLocation.fromNamespaceAndPath(zeta.modid, "advancement_flag"), () -> ZetaGeneralConfig.enableModdedAdvancements));

		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath(zeta.modid, "flag"), FLAG_CONDITION_TYPE);
		ext.registerIngredientSerializer(ResourceLocation.fromNamespaceAndPath(zeta.modid, "flag"), flagIngredientSerializer);

		SyncedFlagHandler.setupFlagManager(this);
	}

	public void clear() {
		flags.clear();
	}

	public void putFlag(ZetaModule module, String flag, boolean value) {
		flags.put(flag, value && module.enabled);
		if(!allFlags.contains(flag)) {
			allFlags.add(flag);
		}
	}

	public void putModuleFlag(ZetaModule module) {
		putFlag(module, module.lowercaseName, true);
	}

	public boolean isValidFlag(String flag) {
		return flags.containsKey(flag);
	}

	public boolean getFlag(String flag) {
		Boolean obj = flags.get(flag);
		return obj != null && obj;
	}

	public Set<String> getAllFlags() {
		return allFlags;
	}
}