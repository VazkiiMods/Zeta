package org.violetmoon.zeta.event.bus;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zetaimplforge.event.load.ForgeZRegister;

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
import java.util.function.Predicate;

// this is quite jank. Basically converts all zeta events to forge ones, then delegates to the forge bus directly
public class ForgeZetaEventBus<Z, F extends Event> extends ZetaEventBus<Z> {

    private final Map<Class<? extends Z>, Function<? extends F, ? extends Z>> forgeToZetaMap = new HashMap<>();
    private final Map<Class<? extends Z>, Function<? extends Z, ? extends F>> zetaToForgeMap = new HashMap<>();
    private final Map<Class<? extends Z>, Class<?>> zetaToForgeEventClass = new HashMap<>();
    private final Map<Class<? extends Z>, Class<?>> generics = new HashMap<>();
    private final Map<Key, Object> convertedHandlers = new HashMap<>();

    private final IEventBus forgeBus;
    private final Class<F> forgeEventRoot; //if Events should implement IModBusEvent

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    public ForgeZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<Z> eventRoot,
                             @Nullable Logger logSpam, IEventBus forgeBus, Class<F> forgeEventRoot) {
        super(subscriberAnnotation, eventRoot, logSpam);
        this.forgeBus = forgeBus;
        this.forgeEventRoot = forgeEventRoot;
    }


    @Override
    protected void subscribeMethod(Method method, Object receiver, Class<?> owningClazz) {
        if (method.getParameterCount() != 1)
            throw arityERR(method);

        Class<?> zetaEventClass = method.getParameterTypes()[0];

        //check if it's already a forge event, or it's a zeta event
        if (!eventRoot.isAssignableFrom(zetaEventClass) && !forgeEventRoot.isAssignableFrom(zetaEventClass))
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

        Consumer<? extends F> consumer = remapMethod(handle, zetaEventClass);
        registerListenerToForgeWithPriorityAndGenerics(owningClazz, consumer, zetaEventClass);
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
    public <T extends Z> T fire(@NotNull T event) {
        forgeBus.post(remapEvent(event, event.getClass()));
        return event;
    }

    @Override
    public <T extends Z> T fire(@NotNull T event, Class<? super T> firedAs) {
        forgeBus.post(remapEvent(event, firedAs));
        return event;
    }

    // reflection hacks and ugly code below. Turn back now. You have been warned

    // good thing is most of this can be removed in 1.21 since Event is an interface there so we can pass zeta events directly. Just need to make zeta event extend Event

    private <Z2 extends Z> F remapEvent(@NotNull Z2 zetaEvent, Class<?> firedAs) {
        Function<? extends Z, ? extends F> zetaToForgeFunc = zetaToForgeMap.get(firedAs);
        if (zetaToForgeFunc == null) {
            // remap is null. no checks because micro optimization. It means it must be a forge event already
            return (F) zetaEvent;
            //throw new RuntimeException("No wrapped forge Event found for Zeta event class. You must register its subclass using registerSubclass. " + firedAs);
        }
        return createForgeEvent(zetaEvent, zetaToForgeFunc);
    }

    // takes a method that takes a zeta event and turns into one that takes a forge event
    private Consumer<? extends F> remapMethod(MethodHandle originalEventConsumer, Class<?> zetaEventClass) {
        Function<? extends F, ? extends Z> forgeToZetaFunc = forgeToZetaMap.get(zetaEventClass);
        if (forgeToZetaFunc == null) {
            // no remap needed
            if (forgeEventRoot.isAssignableFrom(zetaEventClass)) {
                forgeToZetaFunc = event -> (Z) event;
            } else
                throw new RuntimeException("No forge-Event-wrapping constructor found for Zeta event class. You must register its subclass using registerSubclass. " + zetaEventClass);
        }
        return createForgeConsumer(originalEventConsumer, forgeToZetaFunc, zetaEventClass);
    }

    private <T extends Z> F createForgeEvent(@NotNull Z event, Function<T, ? extends F> function) {
        return function.apply((T) event);
    }

    private <F2 extends F, Z2 extends Z> Consumer<F2> createForgeConsumer(MethodHandle zetaEventConsumer, Function<F2, Z2> forgeToZetaFunc,
                                                                          Class<?> zetaEventClass) {
        //hack for tick events
        Phase phase = Phase.guessFromClassName(zetaEventClass);
        return event -> {
            try {
                //luckily this phase madness will go away with new neoforge
                if (phase != Phase.NONE && event instanceof TickEvent te) {
                    if (phase == Phase.START && te.phase != TickEvent.Phase.START) return;
                    if (phase == Phase.END && te.phase != TickEvent.Phase.END) return;
                }
                zetaEventConsumer.invoke(forgeToZetaFunc.apply(event));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    // for generic events
    public <S extends Z, C extends Z> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass, Class<?> genericClass) {
        registerSubClass(eventClass, zetaEventClass);
        generics.put(eventClass, genericClass);
    }

    public <S extends Z, C extends Z> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass) {

        Function<F, S> wrappingConstructor = findForgeWrapper(zetaEventClass, eventClass);
        registerSubClass(eventClass, zetaEventClass, wrappingConstructor);
    }

    public <S extends Z, C extends Z> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass,
                                                            Function<? extends F, S> constructor, Class<?> genericClass) {
        registerSubClass(eventClass, zetaEventClass, constructor);
        generics.put(eventClass, genericClass);
    }

    public <S extends Z, C extends Z> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass,
                                                            @Nullable Function<? extends F, S> constructor) {
        Object old1 = null;
        Object old2 = null;
        if (constructor != null) {
            old1 = forgeToZetaMap.put(eventClass, constructor);
        }
        Function<? extends Z, ? extends F> zetaToForge = findWrappedForgeEvent(zetaEventClass, eventClass);
        if (zetaToForge == null) {
            zetaToForge = findZetaWrapper(zetaEventClass);
        }

        if (zetaToForge != null) {
            old2 = zetaToForgeMap.put(eventClass, zetaToForge);
        }

        if (old1 != null || old2 != null) {
            throw new RuntimeException("Event class " + eventClass + " already registered");
        }
    }


    private <Z2 extends Z, F2 extends F> Function<F2, Z2> findForgeWrapper(Class<Z2> zetaEventClass, Class<? extends Z> baseZetaEventClass) {

        // if it's an Event already ust returns the no argument constructor
        if (forgeEventRoot.isAssignableFrom(zetaEventClass)) {
            zetaToForgeEventClass.put(baseZetaEventClass, zetaEventClass);
            return event -> (Z2) event;
        }

        // Find the constructor that takes a single parameter of type A
        for (Constructor<?> constructor : zetaEventClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && forgeEventRoot.isAssignableFrom(parameterTypes[0])) {
                return event -> {
                    try {
                        return (Z2) constructor.newInstance(event);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create new instance of event class " + zetaEventClass, e);
                    }
                };
            }
        }
        throw new RuntimeException("No forge-Event-wrapping constructor found for Zeta event class " + zetaEventClass);
    }

    private <Z2 extends Z, F2 extends F> Function<Z2, F2> findZetaWrapper(Class<Z2> zetaEventClass) {
        // if it's an Event already ust returns the no argument constructor

        // Find the constructor that takes a single parameter of type A
        for (Constructor<?> constructor : zetaEventClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && eventRoot.isAssignableFrom(parameterTypes[0])) {
                return event -> {
                    try {
                        return (F2) constructor.newInstance(event);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create new instance of event class " + zetaEventClass, e);
                    }
                };
            }
        }

        if (forgeEventRoot.isAssignableFrom(zetaEventClass)) {
            return null;
        }

        throw new RuntimeException("No Zeta-Event-wrapping constructor found for Zeta event class " + zetaEventClass);
    }


    private <Z2 extends Z, F2 extends F> Function<Z2, F2> findWrappedForgeEvent(Class<Z2> zetaEventClass, Class<? extends Z> baseZetaEventClass) {

        Field eventField = findFieldInClassHierarchy(zetaEventClass, f ->
                forgeEventRoot.isAssignableFrom(f.getType()));
        if (eventField != null) {
            //hack
            eventField.setAccessible(true);
            zetaToForgeEventClass.put(baseZetaEventClass, eventField.getType());
            return instance -> {
                try {
                    return (F2) eventField.get(instance);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }

        //tries to unwrap first. Then if its already a forge event we just keep it
        if (forgeEventRoot.isAssignableFrom(zetaEventClass)) {
            zetaToForgeEventClass.put(baseZetaEventClass, zetaEventClass);
            return null;
        }

        throw new RuntimeException("No wrapped forge Event found for Zeta event class " + zetaEventClass);
    }

    public static Field findFieldInClassHierarchy(Class<?> clazz, Predicate<Field> predicate) {
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                if (predicate.test(f)) {
                    return f;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    //bad string based conversions stuff mess

    //TODO: refactor in 1.21 using interfaces and stuff. This is just here for now as i want to keep binary compatibility

    private void registerListenerToForgeWithPriorityAndGenerics(Class<?> owningClazz, Consumer<? extends Event> consumer, Class<?> zetaEventClass) {
        EventPriority priority = guessPriorityFromClassName(owningClazz);
        Class<?> gen = generics.get(owningClazz);
        Class eventType = zetaToForgeEventClass.get(zetaEventClass);
        if (eventType == null) {
            throw new RuntimeException("No event type found for " + zetaEventClass);
        }
        if (gen != null) {
            forgeBus.addGenericListener(gen, priority, false, eventType, (Consumer<GenericEvent>) consumer);
        } else {
            forgeBus.addListener(priority, false, eventType, consumer);
        }
    }


    private enum Phase {
        NONE, START, END;

        private static Phase guessFromClassName(Class<?> zetaEventClass) {
            String simpleName = zetaEventClass.getSimpleName();
            if (simpleName.equals("Start")) {
                return START;
            } else if (simpleName.equals("End")) {
                return END;
            } else {
                return NONE;
            }
        }
    }


    private static final Map<Class<?>, EventPriority> CACHE = new HashMap<>();

    private static EventPriority guessPriorityFromClassName(Class<?> zetaEventClass) {
        return CACHE.computeIfAbsent(zetaEventClass, cl -> {
            String simpleName = zetaEventClass.getSimpleName();
            for (EventPriority p : EventPriority.values()) {
                String name = WordUtils.capitalizeFully(p.name().toLowerCase());
                if (simpleName.endsWith(name)) {
                    return p;
                }
            }
            return EventPriority.NORMAL;
        });
    }

}
