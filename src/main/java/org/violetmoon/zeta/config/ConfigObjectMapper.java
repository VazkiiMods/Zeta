package org.violetmoon.zeta.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.quark.base.config.type.IConfigType;
import org.violetmoon.zeta.module.ZetaModule;

public class ConfigObjectMapper {
	public static List<Field> walkModuleFields(Class<?> clazz) {
		List<Field> list = new ArrayList<>();
		while(clazz != ZetaModule.class && clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			list.addAll(Arrays.asList(fields));

			clazz = clazz.getSuperclass();
		}

		return list;
	}

	public static Object getField(Object owner, Field field) {
		Object receiver = Modifier.isStatic(field.getModifiers()) ? null : owner;

		try {
			return field.get(receiver);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setField(Object owner, Field field, Object value) {
		Object receiver = Modifier.isStatic(field.getModifiers()) ? null : owner;

		try {
			field.set(receiver, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void readInto(SectionDefinition.Builder sect, Object obj, List<Consumer<IZetaConfigInternals>> databindings, ConfigFlagManager cfm) {
		if(obj instanceof ZetaModule zm)
			readInto(sect, obj, zm, databindings, cfm);
		else
			readInto(sect, obj, null, databindings, cfm);
	}

	public static void readInto(SectionDefinition.Builder sect, Object obj, @Nullable ZetaModule enclosingModule, List<Consumer<IZetaConfigInternals>> databindings, ConfigFlagManager cfm) {
		for(Field field : walkModuleFields(obj.getClass())) {
			Config config = field.getAnnotation(Config.class);

			if(config == null)
				continue;

			field.setAccessible(true);

			//name
			String displayName;
			if(config.name().isEmpty())
				displayName = WordUtils.capitalizeFully(field.getName().replaceAll("(?<=.)([A-Z])", " $1"));
			else
				displayName = config.name();

			//comments
			Config.Min min = field.getDeclaredAnnotation(Config.Min.class);
			Config.Max max = field.getDeclaredAnnotation(Config.Max.class);

			List<String> comment = new ArrayList<>(4);
			if(!config.description().isEmpty())
				comment.addAll(List.of(config.description().split("\n")));

			if(min != null || max != null) {
				NumberFormat format = DecimalFormat.getNumberInstance(Locale.ROOT);
				String minPart = min == null ? "(" : ((min.exclusive() ? "(" : "[") + format.format(min.value()));
				String maxPart = max == null ? ")" : (format.format(max.value()) + (max.exclusive() ? ")" : "]"));
				comment.add("Allowed values: " + minPart + "," + maxPart);
			}

			//default value
			Object defaultValue = getField(obj, field);
			if(defaultValue == null)
				throw new IllegalArgumentException("@Config fields can't have null default values - field " + field.getName() + " obj " + obj.getClass());

			//validators
			Config.Condition condition = field.getDeclaredAnnotation(Config.Condition.class);
			Predicate<Object> restriction = restrict(min, max, condition);

			if(defaultValue instanceof IConfigType configType) {
				//it's a subtree

				sect.addSubsection(subsectionBuilder -> {
					subsectionBuilder
						.name(displayName.toLowerCase(Locale.ROOT).replace(" ", "_"))
						.englishDisplayName(displayName)
						.comment(comment)
						.hint(configType); //<- the HINT system... a little awkward

					//walk inside the ConfigType and look for its @Config annotations, add databinders, etc
					readInto(subsectionBuilder, configType, enclosingModule, databindings, cfm);
				});

				//since databinders are called in order, this will run after the configtype's @Config-annotated
				//fields have been brought up-to-date
				databindings.add(z -> configType.onReload(enclosingModule, cfm));
			} else {
				//it's a leaf node

				ValueDefinition<?> def = sect.addValue(defBuilder -> {
					defBuilder
						.name(displayName)
						.englishDisplayName(displayName)
						.comment(comment)
						.defaultValue(defaultValue)
						.validator(restriction);
				});

				//register a data binder
				databindings.add(z -> setField(obj, field, z.get(def)));

				//does this config option also bind to a flag?
				String flag = config.flag();
				if(!flag.isEmpty()) {
					if(enclosingModule == null)
						throw new IllegalArgumentException("Only ZetaModules can have `@Config(flag = ...)` annotations." +
						"\nClass: " + obj.getClass() +
						"\nField: " + field);

					ValueDefinition<Boolean> defBool = def.downcast(Boolean.class);
					if(defBool == null)
						throw new IllegalArgumentException("Only boolean fields can be annotated with `@Config(flag = ...)`." +
						"\nClass: " + obj.getClass() +
						"\nField: " + field);

					//add the flag now
					cfm.putFlag(enclosingModule, flag, true);

					//and add a reloader for it later
					databindings.add(z -> cfm.putFlag(enclosingModule, flag, z.get(defBool)));
				}
			}
		}
	}

	private static Predicate<Object> restrict(@Nullable Config.Min min, @Nullable Config.Max max, @Nullable Config.Condition condition) {
		double minVal = min == null ? -Double.MAX_VALUE : min.value();
		double maxVal = max == null ? Double.MAX_VALUE : max.value();
		boolean minExclusive = min != null && min.exclusive();
		boolean maxExclusive = max != null && max.exclusive();

		Predicate<Object> pred = (o) -> restrict(o, minVal, minExclusive, maxVal, maxExclusive);
		if(condition != null){
			try {
				Constructor<? extends Predicate<Object>> constr = condition.value().getDeclaredConstructor();
				constr.setAccessible(true);
				Predicate<Object> additionalPredicate = constr.newInstance();
				pred = pred.and(additionalPredicate);
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to parse config Predicate annotation: ", e);
			}
		}
		return pred;
	}

	private static boolean restrict(Object o, double minVal, boolean minExclusive, double maxVal, boolean maxExclusive) {
		if (o == null)
			return false;

		if (o instanceof Number num) {
			double val = num.doubleValue();
			if (minExclusive) {
				if (minVal >= val)
					return false;
			} else if (minVal > val)
				return false;

			if (maxExclusive) {
				if (maxVal <= val)
					return false;
			} else if (maxVal < val)
				return false;
		}

		return true;
	}
}
