package org.violetmoon.zeta.event.bus;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

/**
 * A polymorphic event bus. Events can be fired under one of their supertypes, allowing a sort of API/impl split of events.
 *
 * Due to implementation complexity, there is unfortunately no support for:
 * - generic events (like Forge's RegistryEvent<T>)
 * - registering an anonymous `Consumer` (like Forge's "addListener" method)
 * Supported Java reflection APIs don't expose this information. Forge can only get at it with a library internally using sun.misc.Unsafe.
 */
public class FabricZetaEventBus<E> extends ZetaEventBus<E> {

	private final Map<Class<? extends E>, Listeners> listenerMap = new HashMap<>();

	/**
	 * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
	 * @param eventRoot            The superinterface of all events fired on this bus.
	 * @param logSpam
	 */
	public FabricZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<E> eventRoot, @Nullable Logger logSpam) {
		super(subscriberAnnotation, eventRoot, logSpam);
	}

	@Override
	protected void subscribeMethod(Method m, Object receiver, Class owningClazz) {
		getListenersFor(m).subscribe(receiver, owningClazz, m);
	}

	@Override
	protected void unsubscribeMethod(Method m, Object receiver, Class owningClazz) {
		getListenersFor(m).unsubscribe(receiver, owningClazz, m);
	}

	/**
	 * Fires an event on the event bus. Each subscriber will be visited in order.
	 */
	public <T extends E> T fire(@NotNull T event) {
		Listeners subs = listenerMap.get(event.getClass());
		if(subs != null) {
			if(logSpam != null)
				logSpam.info("Dispatching {} to {} listener{}", logSpamSimpleName(event.getClass()), subs.size(), subs.size() > 1 ? "s" : "");

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
				logSpam.info("Dispatching {} (as {}) to {} listener{}", logSpamSimpleName(event.getClass()), logSpamSimpleName(firedAs), subs.size(), subs.size() > 1 ? "s" : "");

			subs.doFire(event);
		}

		return event;
	}

	//this is really silly
	private String logSpamSimpleName(Class<?> clazz) {
		String[] split = clazz.getName().split("\\.");
		return split[split.length - 1];
	}

	public <T extends E> T fireExternal(@NotNull T event, Class<? super T> firedAs) {
		event = fire(event, firedAs);

		if(event instanceof Cancellable cancellable && cancellable.isCanceled())
			return event;
		else{
			throw new RuntimeException();
			//TODO: re add. this shuld be put in loader specific bus code
			//return z.fireExternalEvent(event); // Interfaces with the platform-specific event bus utility
		}

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

}
