package org.violetmoon.zeta.config;

public interface IZetaConfigInternals {
	<T> T get(ValueDefinition<T> definition);
	<T> void set(ValueDefinition<T> definition, T value);

	void flush();

	//for debouncing Forge ConfigChangedEvent cause its glitchy af
	default long debounceTime() {
		return 0;
	}
}
