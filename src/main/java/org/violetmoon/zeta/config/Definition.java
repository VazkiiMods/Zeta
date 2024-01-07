package org.violetmoon.zeta.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Common superclass of a... "thing" in a config definition (a value or section).
 *
 * @see org.violetmoon.zeta.config.SectionDefinition
 * @see org.violetmoon.zeta.config.ValueDefinition
 */
public abstract class Definition {

	// Name used in config file
	public final String name;

	// Name used for translation keys, building paths, etc
	public final String lowercaseName;

	// Name used in GUIs in English
	public final String englishDisplayName;

	public final List<String> comment;

	//late-bound from setParent :/
	public @Nullable SectionDefinition parent = null;
	public List<String> path = new ArrayList<>(1);
	private String configNameKey; //purely caching a kinda-expensive string join operation
	private String configDescKey;

	//used in ClientConfigManager
	public final @Nullable Object hint;

	public Definition(Definition.Builder<?, ? extends Definition> builder) {
		this.name = Preconditions.checkNotNull(builder.name, "Definitions require a name");
		this.lowercaseName = builder.lowercaseName != null ? builder.lowercaseName : name.toLowerCase(Locale.ROOT).replace(' ', '_');
		this.englishDisplayName = builder.englishDisplayName != null ? builder.englishDisplayName :
			//if the name has an uppercase letter or a space, assume it's already a display-ready name
			(name.matches("[A-Z ]") ? name : WordUtils.capitalizeFully(name.replaceAll("(?<=.)([A-Z])", " $1")));

		this.comment = builder.comment;
		this.hint = builder.hint;
	}

	public void setParent(SectionDefinition parent) {
		this.parent = parent;
		this.path = new ArrayList<>(5);

		Definition d = this;
		do {
			if(!d.lowercaseName.isEmpty()) //kinda cheeky, avoids adding the "root" element the whole config is wrapped in to the path
				path.add(d.lowercaseName);
			d = d.parent;
		} while(d != null);

		Collections.reverse(path);
		((ArrayList<?>) path).trimToSize();

		//translation keys
		String stem = "quark.config." + String.join(".", path);
		configNameKey = stem + ".name";
		configDescKey = stem + ".desc";
	}

	public String[] commentToArray() {
		return comment == null ? new String[0] : comment.toArray(new String[0]);
	}

	public String commentToString() {
		return comment == null ? "" : String.join("\n", comment);
	}

	/// TRANSLATIONS ///

	private static final boolean translationDebug = System.getProperty("zeta.configTranslations", null) != null;

	//note this is SHARED code, so i have to use I18n.get at arm's length
	public final String getTranslatedDisplayName(Function<String, String> i18nDotGet) {
		if(translationDebug)
			return configNameKey;

		String local = i18nDotGet.apply(configNameKey);
		if(local.isEmpty() || local.equals(configNameKey))
			return englishDisplayName;

		return local;
	}

	public final List<String> getTranslatedComment(Function<String, String> i18nDotGet) {
		if(translationDebug)
			return List.of(configDescKey);

		String local = i18nDotGet.apply(configDescKey);
		if(local.isEmpty() || local.equals(configDescKey))
			return comment;

		return List.of(local.split("\n"));
	}

	/// EXTENSIBLE BUILDER ///
	// hope this isnt too weird. the generics are to allow SectionDefinition.Builder and
	// ValueDefinition.Builder to extend this class, while still having them work ergonomically as builders

	public abstract static class Builder<B extends Builder<B, T>, T extends Definition> {
		protected @Nullable String name;
		protected @Nullable String lowercaseName;
		protected @Nullable String englishDisplayName;

		protected List<String> comment = new ArrayList<>(2);
		protected @Nullable Object hint;

		public abstract T build();

		public B name(String name) {
			this.name = name;
			return downcast();
		}

		public B lowercaseName(String lowercaseName) {
			this.lowercaseName = lowercaseName;
			return downcast();
		}

		public B englishDisplayName(String displayName) {
			this.englishDisplayName = displayName;
			return downcast();
		}

		public B comment(String comment) {
			return comment(List.of(comment));
		}

		public B comment(List<String> comment) {
			comment.stream()
				.flatMap(line -> Stream.of(line.split("\n")))
				.filter(line -> !line.trim().isEmpty())
				.forEach(this.comment::add);

			return downcast();
		}

		public B hint(Object hint) {
			this.hint = hint;
			return downcast();
		}

		@SuppressWarnings("unchecked")
		protected <X> X downcast() {
			return (X) this;
		}
	}
}
