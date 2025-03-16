package org.violetmoon.zetaimplforge.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;

import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeBackedConfig implements IZetaConfigInternals {
	private final Map<ValueDefinition<?>, ForgeConfigSpec.ConfigValue<?>> definitionsToValues = new HashMap<>();

	public ForgeBackedConfig(SectionDefinition rootSection, ForgeConfigSpec.Builder forgeBuilder) {
		walkSection(rootSection, forgeBuilder, true);
	}

	private void walkSection(SectionDefinition sect, ForgeConfigSpec.Builder builder, boolean root) {
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

	private <T> void addValue(ValueDefinition<T> val, ForgeConfigSpec.Builder builder) {
		builder.comment(val.commentToArray());

		ForgeConfigSpec.ConfigValue<?> forge;
		if(val.defaultValue instanceof List<?> list)
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
}
