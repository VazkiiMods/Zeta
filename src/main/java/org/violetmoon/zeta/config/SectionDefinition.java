package org.violetmoon.zeta.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SectionDefinition extends Definition {
	public final @NotNull Map<String, SectionDefinition> subsections;
	public final @NotNull Map<String, ValueDefinition<?>> values;

	public SectionDefinition(SectionDefinition.Builder builder) {
		super(builder);

		this.subsections = builder.subsections;
		this.values = builder.values;
	}

	public void finish() {
		//Remove empty subsections
		getSubsections().removeIf(sub -> !sub.getAllChildren().iterator().hasNext());

		//Link up the graph
		getAllChildren().forEach(child -> child.setParent(this));

		//Recurse
		getSubsections().forEach(SectionDefinition::finish);
	}

	public @Nullable ValueDefinition<?> getValue(String name) {
		return values.get(name);
	}

	public <T> @Nullable ValueDefinition<T> getValue(String name, Class<T> type) {
		ValueDefinition<?> value = getValue(name);
		return value == null ? null : value.downcast(type);
	}

	//ugh
	@SuppressWarnings("unchecked")
	public <T> @Nullable ValueDefinition<T> getValueErased(String name, Class<?> type) {
		return (ValueDefinition<T>) getValue(name, type);
	}

	public Collection<SectionDefinition> getSubsections() {
		return subsections.values();
	}

	public Collection<ValueDefinition<?>> getValues() {
		return values.values();
	}

	public Iterable<Definition> getAllChildren() {
		return Iterables.concat(getSubsections(), getValues());
	}

	@Override
	public String toString() {
		return "SectionDefinition{" + name + " (" + subsections.size() + " subsections, " + values.size() + " values)}";
	}

	public static class Builder extends Definition.Builder<SectionDefinition.Builder, SectionDefinition> {
		protected final Map<String, SectionDefinition> subsections = new LinkedHashMap<>();
		protected final Map<String, ValueDefinition<?>> values = new LinkedHashMap<>();

		@Override
		public SectionDefinition build() {
			return new SectionDefinition(this);
		}

		public SectionDefinition.Builder addSubsection(SectionDefinition value) {
			subsections.put(value.name, value);
			return this;
		}

		public SectionDefinition.Builder addValue(ValueDefinition<?> value) {
			values.put(value.name, value);
			return this;
		}

		// more convenient sometimes
		public SectionDefinition addSubsection(Consumer<Builder> buildAction) {
			SectionDefinition.Builder childBuilder = new Builder();
			buildAction.accept(childBuilder);
			SectionDefinition child = childBuilder.build();

			addSubsection(child);
			return child;
		}

		public <T> ValueDefinition<T> addValue(Consumer<ValueDefinition.Builder<T>> buildAction) {
			ValueDefinition.Builder<T> childBuilder = new ValueDefinition.Builder<>();
			buildAction.accept(childBuilder);
			ValueDefinition<T> child = childBuilder.build();

			addValue(child);
			return child;
		}

	}
}
