package org.violetmoon.zetaimplforge.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.Serial;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.Function;

public class TerribleForgeConfigHackery {
	private static boolean pleaseWorkPleaseImBegging = false;
	//private static final Method SET_CONFIG_DATA = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfigData", Function.class, CommentedConfig.class) ;
	// private static final Method SET_CONFIG_DATA = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfig", LoadedConfig.class, FunctionalInterface.class);
	private static final Method SETUP_CONFIG_FILE = ObfuscationReflectionHelper.findMethod(ConfigTracker.class, "setupConfigFile", ModConfig.class, Path.class);

	// TODO: Replace the name string + not 100% sure about this
	public static void registerAndLoadConfigEarlierThanUsual(ModConfigSpec spec, String modID) {
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		ModConfig modConfig = ConfigTracker.INSTANCE.registerConfig(ModConfig.Type.COMMON, spec, container);

		ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, Path.of(modConfig.getFileName()));

		//same stuff that forge config tracker does
		//read config without setting file watcher which could cause resets. forge will load it later
		CommentedFileConfig configData = readConfig(ConfigTracker.INSTANCE, FMLPaths.CONFIGDIR.get(), modConfig);
		//CommentedFileConfig configData = handler.reader(FMLPaths.CONFIGDIR.get()).apply( modConfig);

		container.acceptEvent(new ModConfigEvent.Loading(modConfig));

		/*
		SET_CONFIG_DATA.setAccessible(true);
		try {
			SET_CONFIG_DATA.invoke(modConfig, new LoadedConfig(configData, modConfig.getFullPath(), modConfig), ModConfigEvent.Loading::new);
		} catch (Exception ignored) {}
		//container.dispatchConfigEvent(IConfigEvent.loading(this.config));

		 */
			configData.save();

	}

	//we need this so we dont add a second file watcher. Same as handler::reader
	private static CommentedFileConfig readConfig(ConfigTracker handler, Path configBasePath, ModConfig c) {
		Path configPath = configBasePath.resolve(c.getFileName());
		CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
			preserveInsertionOrder().
			autosave().
			onFileNotFound((newfile, configFormat) -> {
				try {
					SETUP_CONFIG_FILE.invoke(handler, c, newfile);
					return true;
				} catch (Exception e) {
					throw new ConfigLoadingException(c, e);
				}
			}).
			writingMode(WritingMode.REPLACE).
			build();
		try {
			configData.load();
		}
		catch (Exception ex) {
			throw new ConfigLoadingException(c, ex);
		}
		return configData;
	}

	private static class ConfigLoadingException extends RuntimeException {
		@Serial
		private static final long serialVersionUID = 1554369973578001612L;

		public ConfigLoadingException(ModConfig config, Exception cause) {
			super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
		}
	}
}