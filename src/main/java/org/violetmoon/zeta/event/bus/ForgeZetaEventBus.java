package org.violetmoon.zeta.event.bus;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.play.loading.ZAttachCapabilities;

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

        //check if it's already a forge event or it's a zeta event
        if (!eventRoot.isAssignableFrom(eventType) || !Event.class.isAssignableFrom(eventType))
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

        Consumer<? extends Event> consumer = remapMethod(handle, eventType);
        registerListenerToForgeWithPriorityAndGenerics(owningClazz, consumer);
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

    // reflection hacks and ugly code below. Turn back now. You have been warned

    // good thing is most of this can be removed in 1.21 since Event is an interface there so we can pass zeta events directly. Just need to make zeta event extend Event

    private <T extends E> Event remapEvent(@NotNull T event, Class<?> firedAs) {
        Function<? extends E, ? extends Event> zetaToForgeFunc = zetaToForgeMap.get((Class<? extends E>) firedAs);
        if (zetaToForgeFunc == null) {
            throw new RuntimeException("No wrapped forge Event found for Zeta event class. You must register its subclass using registerSubclass. " + firedAs);
        }
        return createForgeEvent(event, zetaToForgeFunc);
    }

    // takes a method that takes a zeta event and turns into one that takes a forge event
    private Consumer<? extends Event> remapMethod(MethodHandle originalEventConsumer, Class<?> zetaEventClass) {
        // if it's already a forge event, just call it
        if (Event.class.isAssignableFrom(zetaEventClass)) {
            return event -> {
                try {
                    originalEventConsumer.invoke(event);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }

        Function<? extends Event, ? extends E> forgeToZetaFunc = forgeToZetaMap.get(zetaEventClass);
        if (forgeToZetaFunc == null) {
            throw new RuntimeException("No forge-Event-wrapping constructor found for Zeta event class. You must register its subclass using registerSubclass. " + zetaEventClass);
        }
        return createForgeConsumer(originalEventConsumer, forgeToZetaFunc, zetaEventClass);
    }

    private <T extends E> Event createForgeEvent(@NotNull E event, Function<T, ? extends Event> function) {
        return function.apply((T) event);
    }

    private <Z extends E, F extends Event> Consumer<F> createForgeConsumer(MethodHandle zetaEventConsumer, Function<F, Z> forgeToZetaFunc,
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


    public <S extends E, C extends E> void registerSubClass(Class<C> eventClass, Class<S> zetaEventClass) {
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

    //bad string based conversions stuff mess

    //TODO: refactor in 1.21 using interfaces and stuff. This is just here for now as i want to keep binary compatibility

    private void registerListenerToForgeWithPriorityAndGenerics(Class<?> owningClazz, Consumer<? extends Event> consumer) {
        EventPriority priority = guessPriorityFromClassName(owningClazz);
        //harcoded caps bs. Alternatively a registerGenerics method could have been added to the bus.
        Class<?> generics = null;
        if (owningClazz.isAssignableFrom(ZAttachCapabilities.BlockEntityCaps.class)) {
            generics = BlockEntity.class;
        }
        if (owningClazz.isAssignableFrom(ZAttachCapabilities.ItemStackCaps.class)) {
            generics = ItemStack.class;
        }
        if (owningClazz.isAssignableFrom(ZAttachCapabilities.LevelCaps.class)) {
            generics = Level.class;
        }
        if (generics != null) {
            forgeBus.addGenericListener(generics, priority, (Consumer<GenericEvent>) consumer);
        } else {
            forgeBus.addListener(priority, consumer);
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
