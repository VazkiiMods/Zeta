package org.violetmoon.zetaimplforge.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;

import java.io.Serial;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgeBackedConfig implements IZetaConfigInternals {

    private final Map<ValueDefinition<?>, ForgeConfigSpec.ConfigValue<?>> definitionsToValues = new HashMap<>();
    public final ForgeConfigSpec forgeSpec;
    private final ModConfig modConfig;


    public ForgeBackedConfig(SectionDefinition rootSection) {
        var builder = new ForgeConfigSpec.Builder();
        this.walkSection(rootSection, builder, true);
        this.forgeSpec = builder.build();

        ModContainer container = ModLoadingContext.get().getActiveContainer();
        this.modConfig = new ModConfig(ModConfig.Type.COMMON, forgeSpec, container);
        container.addConfig(modConfig);
    }

    private void walkSection(SectionDefinition sect, ForgeConfigSpec.Builder builder, boolean root) {
        if (!root) {
            builder.comment(sect.commentToArray());
            builder.push(sect.name);
        }

        for (ValueDefinition<?> value : sect.getValues())
            addValue(value, builder);

        for (SectionDefinition subsection : sect.getSubsections())
            walkSection(subsection, builder, false);

        if (!root)
            builder.pop();
    }

    private <T> void addValue(ValueDefinition<T> val, ForgeConfigSpec.Builder builder) {
        builder.comment(val.commentToArray());

        ForgeConfigSpec.ConfigValue<?> forge;
        if (val.defaultValue instanceof List<?> list)
            forge = builder.defineList(val.name, list, val::validate);
        else
            forge = builder.define(List.of(val.name), () -> val.defaultValue, val::validate, val.defaultValue.getClass()); //forge is weird

        definitionsToValues.put(val, forge);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(ValueDefinition<T> definition) {
        ForgeConfigSpec.ConfigValue<T> forge = (ForgeConfigSpec.ConfigValue<T>) definitionsToValues.get(definition);
        return forge.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(ValueDefinition<T> definition, T value) {
        ForgeConfigSpec.ConfigValue<T> forge = (ForgeConfigSpec.ConfigValue<T>) definitionsToValues.get(definition);
        forge.set(value);
    }

    @Override
    public void flush() {
        //just pick one; they all point to the same FileConfig anyway
        //this dispatches a forge ModConfigEvent.Reloading
        definitionsToValues.values().iterator().next().save();
    }

    @Override
    public void onZetaReady() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        container.dispatchConfigEvent(IConfigEvent.loading(modConfig));
    }

    private static final Method SET_CONFIG_DATA = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfigData", CommentedConfig.class);
    private static final Method SETUP_CONFIG_FILE = ObfuscationReflectionHelper.findMethod(ConfigFileTypeHandler.class,
            "setupConfigFile", ModConfig.class, Path.class, ConfigFormat.class);

    //call this to load the config early
    public void hackilyLoadEarly() {
        //same stuff that forge config tracker does
        ConfigFileTypeHandler handler = modConfig.getHandler();
        //read config without setting file watcher which could cause resets. forge will load it later
        CommentedFileConfig configData = readConfig(handler, FMLPaths.CONFIGDIR.get(), modConfig);
        //CommentedFileConfig configData = handler.reader(FMLPaths.CONFIGDIR.get()).apply( modConfig);

        SET_CONFIG_DATA.setAccessible(true);
        try {
            SET_CONFIG_DATA.invoke(modConfig, configData);
        } catch (Exception ignored) {
        }

        modConfig.save();
    }

    //we need this so we dont add a second file watcher. Same as handler::reader
    private CommentedFileConfig readConfig(ConfigFileTypeHandler handler, Path configBasePath, ModConfig c) {
        Path configPath = configBasePath.resolve(c.getFileName());
        CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                preserveInsertionOrder().
                autosave().
                onFileNotFound((newfile, configFormat) -> {
                    try {
                        return (Boolean) SETUP_CONFIG_FILE.invoke(handler, c, newfile, configFormat);
                    } catch (Exception e) {
                        throw new ConfigLoadingException(c, e);
                    }
                }).
                writingMode(WritingMode.REPLACE).
                build();
        try {
            configData.load();
        } catch (Exception ex) {
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
