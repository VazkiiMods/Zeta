package org.violetmoon.zeta.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.module.ZetaModuleManager;

public class ConfigManager {
	private final Zeta z;
	private final ConfigFlagManager cfm;
	private final SectionDefinition rootConfig;

	//for updating the values of @Config annotations to match the current state of the config
	// and other "listening for config load" purposes
	private final List<Consumer<IZetaConfigInternals>> databindings = new ArrayList<>();
	private Consumer<IZetaConfigInternals> onConfigReloadJEI;

	//ummmmmmm i think my abstraction isn't very good
	private final @Nullable SectionDefinition generalSection;
	private final Map<ZetaCategory, SectionDefinition> categoriesToSections = new HashMap<>();

	private final Map<ZetaCategory, ValueDefinition<Boolean>> categoryEnabledOptions = new HashMap<>();
	private final Map<ZetaModule, ValueDefinition<Boolean>> ignoreAntiOverlapOptions = new HashMap<>();
	private final Map<ZetaModule, ValueDefinition<Boolean>> moduleEnabledOptions = new HashMap<>();

	//state
	private final Set<ZetaCategory> enabledCategories = new HashSet<>();

	public ConfigManager(Zeta z, Object rootPojo) {
		this.z = z;
		this.cfm = new ConfigFlagManager(z);
		ZetaModuleManager modules = z.modules;

		//all modules are enabled by default
		enabledCategories.addAll(modules.getCategories());

		SectionDefinition.Builder rootConfigBuilder = new SectionDefinition.Builder().name("");

		// "general" section
		if(rootPojo == null)
			generalSection = null;
		else {
			//TODO: where to put this lol
			z.loadBus.subscribe(rootPojo).subscribe(rootPojo.getClass());
			z.playBus.subscribe(rootPojo).subscribe(rootPojo.getClass());

			generalSection = rootConfigBuilder.addSubsection(general -> ConfigObjectMapper.readInto(general.name("general"), rootPojo, databindings, cfm));
		}

		// "categories" section, holding the category enablement options
		rootConfigBuilder.addSubsection(categories -> {
			categories.name("categories");
			for(ZetaCategory category : modules.getInhabitedCategories())
				categoryEnabledOptions.put(category, categories.addValue(b -> b.name(category.name).defaultValue(true)));
		});

		// per-category options
		for(ZetaCategory category : modules.getInhabitedCategories()) {
			categoriesToSections.put(category, rootConfigBuilder.addSubsection(categorySectionBuilder -> {
				categorySectionBuilder.name(category.name);
				for(ZetaModule module : modules.modulesInCategory(category)) {
					// module flag
					cfm.putModuleFlag(module);

					// module enablement option
					moduleEnabledOptions.put(module, categorySectionBuilder.addValue(moduleEnabledOptionBuilder -> moduleEnabledOptionBuilder
						.name(module.displayName)
						.englishDisplayName(module.displayName)
						.comment(module.description)
						.defaultValue(module.enabledByDefault)));

					// per-module options
					categorySectionBuilder.addSubsection(moduleSectionBuilder -> {
						moduleSectionBuilder
							.name(module.lowercaseName)
							.englishDisplayName(module.displayName)
							.comment(module.description);

						// @Config options
						ConfigObjectMapper.readInto(moduleSectionBuilder, module, databindings, cfm);

						// anti overlap option
						if(!module.antiOverlap.isEmpty()) {
							ignoreAntiOverlapOptions.put(module, moduleSectionBuilder.addValue(antiOverlapOptionBuilder -> {
								antiOverlapOptionBuilder.name("Ignore Anti Overlap")
									.comment("This feature disables itself if any of the following mods are loaded:")
									.defaultValue(false);

								for(String modid : module.antiOverlap)
									antiOverlapOptionBuilder.comment(" - " + modid);

								antiOverlapOptionBuilder.comment("This is done to prevent content overlap.")
									.comment("You can turn this on to force the feature to be loaded even if the above mods are also loaded.");
							}));
						}
					});
				}
			}));
		}

		//grab any extra flags
		z.playBus.fire(new ZGatherAdditionalFlags(cfm));

		//managing module enablement in one go
		//adding this to the *start* of the list so modules are enabled before anything else runs
		//Its Janky !
		databindings.add(0, i -> {
			categoryEnabledOptions.forEach((category, option) -> setCategoryEnabled(category, i.get(option)));
			ignoreAntiOverlapOptions.forEach((module, option) -> module.ignoreAntiOverlap = !GeneralConfig.useAntiOverlap || i.get(option));
			moduleEnabledOptions.forEach((module, option) -> {
				setModuleEnabled(module, i.get(option));
				cfm.putModuleFlag(module);
			});

			//update extra flags
			z.playBus.fire(new ZGatherAdditionalFlags(cfm));
		});

		this.rootConfig = rootConfigBuilder.build();
		rootConfig.finish();
	}

	public SectionDefinition getRootConfig() {
		return rootConfig;
	}

	// mapping between internal and external representations of the config (??????)

	public @Nullable SectionDefinition getGeneralSection() {
		return generalSection;
	}

	public SectionDefinition getCategorySection(ZetaCategory cat) {
		return categoriesToSections.get(cat);
	}

	public ValueDefinition<Boolean> getCategoryEnabledOption(ZetaCategory cat) {
		return categoryEnabledOptions.get(cat);
	}

	public ValueDefinition<Boolean> getModuleEnabledOption(ZetaModule module) {
		return moduleEnabledOptions.get(module);
	}

	// support for the options added by this class

	private void setCategoryEnabled(ZetaCategory cat, boolean enabled) {
		if(enabled)
			enabledCategories.add(cat);
		else
			enabledCategories.remove(cat);

		//TODO: hacky, just forcing setEnabled to rerun since it checks category enablement
		for(ZetaModule mod : z.modules.modulesInCategory(cat)) {
			mod.setEnabled(z, mod.enabled);
		}
	}

	private void setModuleEnabled(ZetaModule module, boolean enabled) {
		module.setEnabled(z, enabled);
	}

	public boolean isCategoryEnabled(ZetaCategory cat) {
		return enabledCategories.contains(cat);
	}

	// ummm

	public ConfigFlagManager getConfigFlagManager() {
		return cfm;
	}

	public void onReload() {
		IZetaConfigInternals internals = z.configInternals;
		databindings.forEach(c -> c.accept(internals));

		if(onConfigReloadJEI != null)
			onConfigReloadJEI.accept(internals);
	}

	public void setJeiReloadListener(Consumer<IZetaConfigInternals> consumer) {
		this.onConfigReloadJEI = consumer;
		consumer.accept(z.configInternals); //run it now as well
	}
}
