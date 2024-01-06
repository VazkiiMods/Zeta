package org.violetmoon.zeta.config;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValueDefinition<T> extends Definition {
	public final @NotNull T defaultValue;
	public final @Nullable Predicate<Object> validator;

	public ValueDefinition(ValueDefinition.Builder<T> builder) {
		super(builder);

		this.defaultValue = Preconditions.checkNotNull(builder.defaultValue, "ValueDefinition needs a default value");
		this.validator = builder.validator;
	}

	public boolean isOfType(Class<?> clazz) {
		return clazz.isAssignableFrom(defaultValue.getClass());
	}

	@SuppressWarnings("unchecked")
	public <X> @Nullable ValueDefinition<X> downcast(Class<X> newType) {
		if(isOfType(newType))
			return (ValueDefinition<X>) this;
		else
			return null;
	}

	public boolean validate(Object underTest) {
		//you HAVE to start with a nullcheck and a subtype check, lest forge's config api explode into a million pieces
		if(underTest == null)
			return false;

		//TODO: forge's defineList passes each *element* to the validator predicate, not the list itself :/
		// so i need an exemption from isSubtype for lists
		boolean isList = List.class.isAssignableFrom(defaultValue.getClass());
		boolean isSubtype = defaultValue.getClass().isAssignableFrom(underTest.getClass());
		if(!isList && !isSubtype)
			return false;

		if(validator == null)
			return true;
		else
			return validator.test(underTest);
	}

	@Override
	public String toString() {
		return "ValueDefinition{" + name + "}";
	}

	public static class Builder<T> extends Definition.Builder<Builder<T>, ValueDefinition<T>> {
		protected @Nullable T defaultValue;
		protected @Nullable Predicate<Object> validator;

		@Override
		public ValueDefinition<T> build() {
			return new ValueDefinition<>(this);
		}

		public Builder<T> defaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> validator(Predicate<Object> validator) {
			this.validator = validator;
			return this;
		}
	}
}
