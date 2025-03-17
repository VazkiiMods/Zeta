package org.violetmoon.zeta.config;

public interface IZetaConfigInternals {
	<T> T get(ValueDefinition<T> definition);
	<T> void set(ValueDefinition<T> definition, T value);

	void flush();

    default void onZetaReady(){
	}
}
