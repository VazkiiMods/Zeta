package org.violetmoon.zetaimplforge.config;

import com.electronwill.nightconfig.core.file.FileWatcher;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public class ForgeBackedConfig implements IZetaConfigInternals {
	private final Map<ValueDefinition<?>, ModConfigSpec.ConfigValue<?>> definitionsToValues = new HashMap<>();
	private long debounceTime = System.currentTimeMillis();
    private ModConfig config = null;

	public ForgeBackedConfig(SectionDefinition rootSection, ModConfigSpec.Builder forgeBuilder) {
		walkSection(rootSection, forgeBuilder, true);
	}

	private void walkSection(SectionDefinition sect, ModConfigSpec.Builder builder, boolean root) {
		if(!root) {
			builder.comment(sect.commentToArray());
			builder.push(sect.name);
		}

		for(ValueDefinition<?> value : sect.getValues())
			addValue(value, builder);

		for(SectionDefinition subsection : sect.getSubsections())
			walkSection(subsection, builder, false);

		if(!root)
			builder.pop();
	}

	private <T> void addValue(ValueDefinition<T> val, ModConfigSpec.Builder builder) {
		builder.comment(val.commentToArray());

		ModConfigSpec.ConfigValue<?> forge;
		if(val.defaultValue instanceof List<?> list)
			forge = builder.defineList(val.name, list, val::validate);
		else
			forge = builder.define(List.of(val.name), () -> val.defaultValue, val::validate, val.defaultValue.getClass()); //forge is weird

		definitionsToValues.put(val, forge);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(ValueDefinition<T> definition) {
		ModConfigSpec.ConfigValue<T> forge = (ModConfigSpec.ConfigValue<T>) definitionsToValues.get(definition);
		return forge.get();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void set(ValueDefinition<T> definition, T value) {
		ModConfigSpec.ConfigValue<T> forge = (ModConfigSpec.ConfigValue<T>) definitionsToValues.get(definition);
		debounceTime = System.currentTimeMillis();
		forge.set(value);
	}

    public void setModConfig(ModConfig config) {
        this.config = config;
    }

	@Override
	public void flush() {
		//just pick one; they all point to the same FileConfig anyway
		//this dispatches a forge ModConfigEvent.Reloading

        FileWatcher.defaultInstance().removeWatch(this.config.getFullPath());
		definitionsToValues.values().iterator().next().save();
        FileWatcher.defaultInstance().addWatchFuture(this.config.getFullPath(), new SuspiciouslyConfigWatcherShapedClazz(config, this.config.getFullPath()));
	}

    //net.neoforged.fml.config.ConfigWatcher but under a different name so that way we keep all the behavior without having to do things inside of Neoforge that we really shouldnt (Not that we should do this)
    private static class SuspiciouslyConfigWatcherShapedClazz implements Runnable {
        private static final Logger LOGGER = LogUtils.getLogger();

        private static final Method LOAD_CONFIG = ObfuscationReflectionHelper.findMethod(ConfigTracker.class, "loadConfig", ModConfig.class, Path.class, Function.class);
        private static final Field LOCK = ObfuscationReflectionHelper.findField(ModConfig.class, "lock"); // Lock may not be needed, testing is required.

        private final ModConfig modConfig;
        private final Path path;
        private final ClassLoader realClassLoader;

        SuspiciouslyConfigWatcherShapedClazz(ModConfig modConfig, Path path) {
            this.modConfig = modConfig;
            this.path = path;
            this.realClassLoader = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            var previousLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(realClassLoader);
            try {
                try {
                    ((Lock)LOCK.get(modConfig)).lock();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                try {
                    try {
                        LOAD_CONFIG.invoke(null, this.modConfig, this.path, ((Function<ModConfig, ModConfigEvent>) ModConfigEvent.Reloading::new));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    try {
                        ((Lock)LOCK.get(modConfig)).unlock();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                Thread.currentThread().setContextClassLoader(previousLoader);
            }
        }
    }
}
