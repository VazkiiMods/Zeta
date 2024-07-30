package org.violetmoon.zeta.event.bus;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// this is quite jank. Basically converts all zeta events to forge ones, then delegates to the forge bus directly
public class ForgeZetaEventBus<E> extends ZetaEventBus<E> {

    private final Map<Class<? extends E>, Function<? extends Event, ? extends E>> forgeToZetaMap = new HashMap<>();
    private final Map<Class<? extends E>, Function<? extends E, ? extends Event>> zetaToForgeMap = new HashMap<>();

    private final IEventBus forgeBus;
    private final Map<Key, Object> convertedHandlers = new HashMap<>();

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    public ForgeZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<E> eventRoot,
                             @Nullable Logger logSpam, IEventBus forgeBus) {
        super(subscriberAnnotation, eventRoot, logSpam);
        this.forgeBus = forgeBus;
    }


    @Override
    protected void subscribeMethod(Method method, Object receiver, Class<?> owningClazz) {
        if (method.getParameterCount() != 1)
            throw arityERR(method);

        Class<?> eventType = method.getParameterTypes()[0];
        if (!eventRoot.isAssignableFrom(eventType))
            throw typeERR(method);

        MethodHandle handle;
        try {
            handle = MethodHandles.publicLookup().unreflect(method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //fill in the "this" parameter
        if (receiver != null)
            handle = handle.bindTo(receiver);

        Consumer<? extends Event> consumer = remapMethod(handle, (Class<? extends E>) eventType);
        forgeBus.addListener(consumer);
        //store here so we can unregister later
        convertedHandlers.put(new Key(method, receiver, owningClazz), consumer);
    }

    @Override
    protected void unsubscribeMethod(Method m, Object receiver, Class<?> owningClazz) {
        var handler = convertedHandlers.remove(new Key(m, receiver, owningClazz));
        if (handler != null) {
            forgeBus.unregister(handler);
        }
    }

    private record Key(Method method, Object receiver, Class<?> owningClazz) {
    }

    @Override
    public <T extends E> T fire(@NotNull T event) {
        forgeBus.post(remapEvent(event, event.getClass()));
        return event;
    }

    @Override
    public <T extends E> T fire(@NotNull T event, Class<? super T> firedAs) {
        forgeBus.post(remapEvent(event, firedAs));
        return event;
    }

    // reflection hacks below. be warned

    private <T extends E> Event remapEvent(@NotNull T event, Class<?> firedAs) {
        Function<? extends E, ? extends Event> zetaToForgeFunc = zetaToForgeMap.get((Class<? extends E>) firedAs);
        if (zetaToForgeFunc == null) {
            throw new RuntimeException("No wrapped forge Event found for Zeta event class. You must register its subclass using registerSubclass. " + firedAs);
        }
        return createForgeEvent(event, zetaToForgeFunc);
    }

    // takes a method that takes a zeta event and turns into one that takes a forge event
    private Consumer<? extends Event> remapMethod(MethodHandle zetaEventConsumer, Class<? extends E> zetaEventClass) {
        Function<? extends Event, ? extends E> forgeToZetaFunc = forgeToZetaMap.get(zetaEventClass);
        if (forgeToZetaFunc == null) {
            throw new RuntimeException("No forge-Event-wrapping constructor found for Zeta event class. You must register its subclass using registerSubclass. " + zetaEventClass);
        }
        return createForgeConsumer(zetaEventConsumer, forgeToZetaFunc);
    }

    private <T extends E> Event createForgeEvent(@NotNull E event, Function<T, ? extends Event> function) {
        return function.apply((T) event);
    }

    private <Z extends E, F extends Event> Consumer<F> createForgeConsumer(MethodHandle zetaEventConsumer, Function<F, Z> forgeToZetaFunc) {
        return event -> {
            try {
                zetaEventConsumer.invoke(forgeToZetaFunc.apply(event));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }


    public <S extends C, C extends E> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass) {
        forgeToZetaMap.put(eventClass, findWrappingConstructor(zetaEventClass));
        zetaToForgeMap.put(eventClass, findWrappedEvent(zetaEventClass));
    }

    private Function<? extends Event, ? extends E> findWrappingConstructor(Class<? extends E> zetaEventClass) {
        // Find the constructor that takes a single parameter of type A
        for (Constructor<?> constructor : zetaEventClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                return event -> {
                    try {
                        return (E) constructor.newInstance(event);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        }
        throw new RuntimeException("No forge-Event-wrapping constructor found for Zeta event class " + zetaEventClass);
    }


    private Function<? extends E, ? extends Event> findWrappedEvent(Class<? extends E> zetaEventClass) {
        for (Field field : zetaEventClass.getDeclaredFields()) {
            if (Event.class.isAssignableFrom(field.getType())) {
                return instance -> {
                    try {
                        return (Event) field.get(instance);
                    } catch (IllegalAccessException illegalAccessException) {
                        throw new RuntimeException(illegalAccessException);
                    }
                };
            }
        }
        throw new RuntimeException("No wrapped forge Event found for Zeta event class " + zetaEventClass);
    }

}
