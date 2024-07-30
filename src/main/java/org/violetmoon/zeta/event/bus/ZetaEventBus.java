package org.violetmoon.zeta.event.bus;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import com.google.common.base.Preconditions;

/**
 * A polymorphic event bus. Events can be fired under one of their supertypes, allowing a sort of API/impl split of events.
 *
 * Due to implementation complexity, there is unfortunately no support for:
 * - generic events (like Forge's RegistryEvent<T>)
 * - registering an anonymous `Consumer` (like Forge's "addListener" method)
 * Supported Java reflection APIs don't expose this information. Forge can only get at it with a library internally using sun.misc.Unsafe.
 */
public class ZetaEventBus<E> {
	private final Zeta z;
	private final Class<? extends Annotation> subscriberAnnotation;
	private final Class<? extends E> eventRoot;
	private final @Nullable Logger logSpam;

	private final Map<Class<? extends E>, Listeners> listenerMap = new HashMap<>();

	/**
	 * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
	 * @param eventRoot The superinterface of all events fired on this bus.
	 */
	public ZetaEventBus(Zeta z, Class<? extends Annotation> subscriberAnnotation, Class<? extends E> eventRoot, @Nullable Logger logSpam) {
		Preconditions.checkArgument(eventRoot.isInterface(), "Event roots should be an interface");

		this.z = z;
		this.subscriberAnnotation = subscriberAnnotation;
		this.eventRoot = eventRoot;
		this.logSpam = logSpam;
	}

	/**
	 * If the parameter is a Class: subscribes all static methods from it (and its superclasses) to the event bus.
	 * Otherwise, subscribes all non-static methods on that object (and its superclasses) to the event bus.
	 *   (Note that the event bus will hold a reference to this object.)
	 */
	public ZetaEventBus<E> subscribe(@NotNull Object target) {
		Preconditions.checkNotNull(target, "null passed to subscribe");

		Object receiver;
		Class<?> owningClazz;
		if(target instanceof Class<?> clazz) {
			receiver = null;
			owningClazz = clazz;
		} else {
			receiver = target;
			owningClazz = target.getClass();
		}

		streamAnnotatedMethods(owningClazz, receiver == null).forEach(m -> getListenersFor(m).subscribe(receiver, owningClazz, m));
		return this;
	}

	/**
	 * If the parameter is a Class: unsubscribes all static methods from it (and its superclasses) from the event bus.
	 * Otherwise, unsubscribes all non-static methods on that object (and its superclasses) from the event bus.
	 */
	public ZetaEventBus<E> unsubscribe(@NotNull Object target) {
		Preconditions.checkNotNull(target, "null passed to unsubscribe");

		Object receiver;
		Class<?> owningClazz;
		if(target instanceof Class<?> clazz) {
			receiver = null;
			owningClazz = clazz;
		} else {
			receiver = target;
			owningClazz = target.getClass();
		}

		streamAnnotatedMethods(owningClazz, receiver == null).forEach(m -> getListenersFor(m).unsubscribe(receiver, owningClazz, m));
		return this;
	}

	/**
	 * Fires an event on the event bus. Each subscriber will be visited in order.
	 */
	public <T extends E> T fire(@NotNull T event) {
		Listeners subs = listenerMap.get(event.getClass());
		if(subs != null) {
			if(logSpam != null)
				logSpam.info("Dispatching {} to {} listener{}", logspamSimpleName(event.getClass()), subs.size(), subs.size() > 1 ? "s" : "");

			subs.doFire(event);
		}

		return event;
	}

	/**
	 * Fires an event on the event bus. Each subscriber will be visited in order.
	 * Listeners for "firedAs" will be invoked, instead of listeners for the event's own class.
	 * <p>
	 * (The generic should be Class&lt;? super T & ? extends E&gt;, but unfortunately, javac.)
	 */
	public <T extends E> T fire(@NotNull T event, Class<? super T> firedAs) {
		Listeners subs = listenerMap.get(firedAs);
		if(subs != null) {
			if(logSpam != null)
				logSpam.info("Dispatching {} (as {}) to {} listener{}", logspamSimpleName(event.getClass()), logspamSimpleName(firedAs), subs.size(), subs.size() > 1 ? "s" : "");

			subs.doFire(event);
		}

		return event;
	}

	//this is really silly
	private String logspamSimpleName(Class<?> clazz) {
		String[] split = clazz.getName().split("\\.");
		return split[split.length - 1];
	}

	public <T extends E> T fireExternal(@NotNull T event, Class<? super T> firedAs) {
		event = fire(event, firedAs);

		if(event instanceof Cancellable cancellable && cancellable.isCanceled())
			return event;
		else
			return z.fireExternalEvent(event); // Interfaces with the platform-specific event bus utility
	}

	/**
	 * Grabs methods from this class (and its superclasses, recursively) that are annotated with this bus's
	 * annotation; and of the requested staticness.
	 */
	private Stream<Method> streamAnnotatedMethods(Class<?> owningClazz, boolean wantStatic) {
		return Arrays.stream(owningClazz.getMethods())
			.filter(m -> m.isAnnotationPresent(subscriberAnnotation) && ((m.getModifiers() & Modifier.STATIC) != 0) == wantStatic);
	}

	/**
	 * Picks out the "Foo" in "void handleFoo(Foo event)", and gets/creates the Listeners corresponding to that type.
	 */
	@SuppressWarnings("unchecked")
	private Listeners getListenersFor(Method method) {
		if(method.getParameterCount() != 1)
			throw arityERR(method);

		Class<?> eventType = method.getParameterTypes()[0];
		if(!eventRoot.isAssignableFrom(eventType))
			throw typeERR(method);

		return listenerMap.computeIfAbsent((Class<? extends E>) eventType, __ -> new Listeners());
	}

	/**
	 * Mildly overengineered since I want method dispatching to hopefully be low-overhead... don't mind me
	 * MethodHandle is magic free performance right
	 * Pausefrogeline
	 */
	private class Listeners {

		private final Map<Subscriber, MethodHandle> handles = new LinkedHashMap<>();

		private record Subscriber(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			@Override
			public boolean equals(Object object) {
				if(this == object) return true;
				if(object == null || getClass() != object.getClass()) return false;
				Subscriber that = (Subscriber) object;
				return receiver == that.receiver && //<-- object identity compare
					Objects.equals(owningClazz, that.owningClazz) &&
					Objects.equals(method, that.method);
			}

			@Override
			public int hashCode() {
				return System.identityHashCode(receiver) + owningClazz.hashCode() + method.hashCode();
			}

			MethodHandle unreflect() {
				MethodHandle handle;
				try {
					handle = MethodHandles.publicLookup().unreflect(method);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				//fill in the "this" parameter
				if(receiver != null)
					handle = handle.bindTo(receiver);
				return handle;
			}
		}


		void subscribe(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			try {
				handles.computeIfAbsent(new Subscriber(receiver, owningClazz, method), Subscriber::unreflect);
			} catch (Exception e) {
				throw unreflectERR(method, e);
			}
		}

		void unsubscribe(@Nullable Object receiver, Class<?> owningClazz, Method method) {
			handles.remove(new Subscriber(receiver, owningClazz, method));
		}

		int size() {
			return handles.size();
		}

		//just hoisting the instanceof out of the loop.. No profiling just vibes <3
		void doFire(E event) {
			try {
				if(event instanceof Cancellable cancellable)
					doFireCancellable(cancellable);
				else
					doFireNonCancellable(event);
			} catch (Throwable e) {
				throw new RuntimeException("Exception while firing event " + event + ": ", e);
			}
		}

		void doFireCancellable(Cancellable event) throws Throwable {
			for(MethodHandle handle : handles.values()) {
				handle.invoke(event);
				if(event.isCanceled()) break;
			}
		}

		void doFireNonCancellable(E event) throws Throwable {
			for(MethodHandle handle : handles.values())
				handle.invoke(event);
		}
	}

	private RuntimeException arityERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take 1 parameter.", method, null);
	}

	private RuntimeException typeERR(Method method) {
		return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
			" should take an implementor of " + eventRoot.getSimpleName() + ".", method, null);
	}

	private RuntimeException unreflectERR(Method method, Throwable cause) {
		return methodProblem("Exception unreflecting a @" + subscriberAnnotation.getSimpleName() +
			" method, is it public?", method, cause);
	}

	private static RuntimeException methodProblem(String problem, Method method, @Nullable Throwable cause) {
		return new RuntimeException("%s%nMethod class: %s%nMethod name: %s".formatted(
			problem, method.getDeclaringClass().getName(), method.getName()), cause);
	}
}
