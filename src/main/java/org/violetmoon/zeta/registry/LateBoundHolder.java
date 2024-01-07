package org.violetmoon.zeta.registry;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

/**
 * A Holder representing something that hasn't been created yet (so like Holder.Reference),
 * but created at a time we don't have access to the Registry it belongs to yet either, so we can't actually create a Holder.Reference
 *
 * This is fiendishly complicated, I am very sorry.
 */
public class LateBoundHolder<T> implements Holder<T> {

	public LateBoundHolder(ResourceKey<T> key) {
		this.key = key;
	}

	public final ResourceKey<T> key;
	public @Nullable T thing;
	public Registry<T> registry;

	void bind(T thing, Registry<T> registry) {
		this.thing = thing;
		this.registry = registry;
	}

	@Override
	public T value() {
		if(thing == null)
			throw new IllegalStateException("LateBoundHolder: too early");
		return thing;
	}

	@Override
	public boolean isBound() {
		return thing != null;
	}

	@Override
	public boolean is(ResourceLocation p_205713_) {
		return key.location().equals(p_205713_);
	}

	@Override
	public boolean is(ResourceKey<T> p_205712_) {
		return key.equals(p_205712_);
	}

	@Override
	public boolean is(Predicate<ResourceKey<T>> p_205711_) {
		return p_205711_.test(key);
	}

	@Override
	public boolean is(TagKey<T> p_205705_) {
		return false; //um
	}

	@Override
	public Stream<TagKey<T>> tags() {
		return Stream.of();
	}

	@Override
	public Either<ResourceKey<T>, T> unwrap() {
		if(thing == null)
			return Either.left(key);
		else
			return Either.right(thing);
	}

	@Override
	public Optional<ResourceKey<T>> unwrapKey() {
		return Optional.of(key);
	}

	@Override
	public Kind kind() {
		return Kind.REFERENCE; //i guess?
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> what) {
		if(registry == null)
			return false;
		else
			return registry.holderOwner().canSerializeIn(what);
	}
}
