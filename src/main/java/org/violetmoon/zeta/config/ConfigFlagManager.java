package org.violetmoon.zeta.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	public ConfigFlagManager(Zeta zeta) {
		this.zeta = zeta;

		zeta.loadBus.subscribe(this);
	}

	@LoadEvent
	public void onRegister(ZRegister event) {
		CraftingExtensionsRegistry ext = event.getCraftingExtensionsRegistry();

		//TODO: make these Quark-independent
		ext.registerConditionSerializer(new FlagCondition.Serializer(this, new ResourceLocation(zeta.modid, "flag")));
		//Especially this one, which requires quark advancement config option :/
		ext.registerConditionSerializer(new FlagCondition.Serializer(this, new ResourceLocation(zeta.modid, "advancement_flag"), () -> ZetaGeneralConfig.enableModdedAdvancements));

		FlagLootCondition.FlagSerializer flagSerializer = new FlagLootCondition.FlagSerializer(this);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(zeta.modid, "flag"), flagSerializer.selfType);

		ext.registerIngredientSerializer(new ResourceLocation(zeta.modid, "flag"), flagIngredientSerializer);

		//TODO: make this Quark-independent
		SyncedFlagHandler.setupFlagManager(this);
	}

	public void clear() {
		flags.clear();
	}

	public void putFlag(ZetaModule module, String flag, boolean value) {
		flags.put(flag, value && module.isEnabled());
		if(!allFlags.contains(flag)) {
			allFlags.add(flag);
		}
	}

	public void putModuleFlag(ZetaModule module) {
		putFlag(module, module.lowerCaseName(), true);
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
