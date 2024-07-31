package org.violetmoon.zetaimplforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zetaimplforge.event.load.ForgeZCommonSetup;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ForgeEventsRemapper<Z, F extends Event> {

    private final Class<F> forgeEventRoot;
    private final Class<Z> zetaEventRoot;

    private final Map<Class<? extends Z>, Function<? extends F, ? extends Z>> forgeToZetaMap = new Object2ObjectOpenHashMap<>();
    private final Map<Class<? extends Z>, Function<? extends Z, ? extends F>> zetaToForgeMap = new Object2ObjectOpenHashMap<>();
    private final Map<Class<? extends Z>, Class<?>> generics = new Object2ObjectOpenHashMap<>();


    // hack needed to store classes of each Z event to Forge event.
    // Needed because java has type erasure so forge be able to add listeners to anonymous generic lambdas without being passed an event type class
    private final Map<Class<? extends Z>, Class<?>> zetaToForgeEventClassHack = new Object2ObjectOpenHashMap<>();

    public ForgeEventsRemapper(Class<Z> zetaEventRoot, Class<F> forgeEventRoot) {
        this.forgeEventRoot = forgeEventRoot;
        this.zetaEventRoot = zetaEventRoot;
    }

    // reflection hacks and ugly code below. Turn back now. You have been warned

    // good thing is most of this can be removed in 1.21 since Event is an interface there so we can pass zeta events directly. Just need to make zeta event extend Event

    /**
     * Given an event object which is NOT a subtype of Event class, remap it to a forge event
     */
    protected  <Z2 extends Z> F remapEvent(@NotNull Z2 zetaEvent, Class<?> firedAs) {
        Function<? extends Z, ? extends F> zetaToForgeFunc = zetaToForgeMap.get(firedAs);
        if (zetaToForgeFunc == null) {
            // remap is null. no checks because micro optimization. It means it must be a forge event already
            return (F) zetaEvent;
        }
        return createForgeEvent(zetaEvent, zetaToForgeFunc);
    }

    // takes a method that takes a zeta event and turns into one that takes a forge event

    /**
     * Given a MethodHandle of a method which takes a Zeta event, remaps it to a method which takes a Forge event, so we can register it with Forge event bus
     */
    protected Consumer<? extends F> remapMethod(MethodHandle originalEventConsumer, Class<?> zetaEventBaseClass) {
        Function<? extends F, ? extends Z> forgeToZetaFunc = forgeToZetaMap.get(zetaEventBaseClass);
        if (forgeToZetaFunc == null) {
            // no remap needed
            if (forgeEventRoot.isAssignableFrom(zetaEventBaseClass)) {
                forgeToZetaFunc = event -> {
                    try {
                        return (Z) event;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            } else
                throw new RuntimeException("Could not convert Zeta event class " + zetaEventBaseClass + " to Forge event. You must register its subclass using registerSubclass.");
        }
        return createForgeConsumer(originalEventConsumer, forgeToZetaFunc, zetaEventBaseClass);
    }


    // generic bs
    private <T extends Z> F createForgeEvent(@NotNull Z event, Function<T, ? extends F> function) {
        return function.apply((T) event);
    }

    // generic bs
    // Creates the Forge Event Consumer that will be registered with Forge Event Bus
    private <F2 extends F, Z2 extends Z> Consumer<F2> createForgeConsumer(
            MethodHandle zetaEventConsumer, Function<F2, Z2> forgeToZetaFunc, Class<?> zetaEventBaseClass) {
        //hack for tick events
        Phase phase = Phase.guessFromClassName(zetaEventBaseClass);
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
    public  <S extends Z, C extends Z> void registerSubClassWithGeneric(Class<C> baseZetaEventClass, Class<S> forgeZetaEventClass, Class<?> genericClass) {
        registerSubClass(baseZetaEventClass, forgeZetaEventClass);
        generics.put(baseZetaEventClass, genericClass);
    }

    public  <ZF extends Z, ZB extends Z> void registerSubClass(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass) {

        registerSubClass(baseZetaEventClass, forgeZetaEventClass, null);
    }

    public <ZF extends Z, ZB extends Z> void registerSubClassWithGeneric(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                                         Function<? extends F, ZF> constructor, Class<?> genericClass) {
        registerSubClass(baseZetaEventClass, forgeZetaEventClass, constructor);
        generics.put(baseZetaEventClass, genericClass);
    }

    public <ZF extends Z, ZB extends Z> void registerSubClass(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                              @Nullable Function<? extends F, ZF> constructor) {
        Object old1;
        Object old2 = null;
        boolean isNoWrapper = false;

        if ((Class) forgeZetaEventClass == ForgeZCommonSetup.class) {
            int aa = 1;
        }

        if (constructor == null) {
            // if it's an Event already just returns the no argument constructor. Provided it subclasses its abstract impl
            if (forgeEventRoot.isAssignableFrom(forgeZetaEventClass)) {
                zetaToForgeEventClassHack.put(baseZetaEventClass, forgeZetaEventClass);

                if (baseZetaEventClass.isAssignableFrom(forgeZetaEventClass)) {
                    constructor = event -> {
                        try {
                            return (ZF) event;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                } else {
                    constructor = findWrappedZetaEvent(forgeZetaEventClass, baseZetaEventClass);
                }

                isNoWrapper = true;
            }
            if (constructor == null) {
                constructor = findForgeWrapper(forgeZetaEventClass, baseZetaEventClass);
            }
        }
        if (constructor == null) {
            throw new RuntimeException("No Forge-Event-wrapping constructor found for Zeta event class " + forgeZetaEventClass);
        } else {
            old1 = forgeToZetaMap.put(baseZetaEventClass, constructor);
        }

        Function<? extends Z, ? extends F> zetaToForge = null;
        if (!isNoWrapper) {
            zetaToForge = findWrappedForgeEvent(forgeZetaEventClass, baseZetaEventClass);
        }

        if (zetaToForge == null) {
            zetaToForge = findZetaWrapper(forgeZetaEventClass);
        }

        if (zetaToForge != null) {
            old2 = zetaToForgeMap.put(baseZetaEventClass, zetaToForge);
        }

        if (old1 != null || old2 != null) {
            throw new RuntimeException("Event class " + baseZetaEventClass + " already registered");
        }
    }

    // This is where the magic happens

    // Explanation:
    // The whole point of these methods is to automatically convert a Zeta event to a Forge event, and vice versa.
    // This is done since we can only have zeta event consumer methods in our common code, however we can only register to the forge bus a consumer of the forge Event class.
    // For this reason here we have some functions that, with a lot of assumptions, try to find wrappers and unwrapper functions for each Forge-Zeta classes pair

    /**
     * @param zetaEventClass     i.e: ForgeZClientSetup.class
     * @param baseZetaEventClass i.e: ZClientSetup.class
     *                           <p>
     *                           Attempts to find a constructor of zetaEventClass which takes an Event as a parameter. This is used for simple forge Event wrappers
     *                           in this example the one found will be new ForgeZClientSetup(FMLClientSetupEvent event)
     */
    private <Z2 extends Z, F2 extends F> Function<F2, Z2> findForgeWrapper(Class<Z2> zetaEventClass, Class<? extends Z> baseZetaEventClass) {

        // Find the constructor that takes a single parameter of type A
        for (Constructor<?> constructor : zetaEventClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && forgeEventRoot.isAssignableFrom(parameterTypes[0])) {
                zetaToForgeEventClassHack.put(baseZetaEventClass, parameterTypes[0]);

                return event -> {
                    try {
                        return (Z2) constructor.newInstance(event);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create new instance of event class " + zetaEventClass, e);
                    }
                };
            }
        }
        return null;
    }

    /**
     * @param zetaEventClass i.e: ForgeZRegister.class
     *                       <p>
     *                       Tries to find a constructor that takes a single parameter with type of Zeta event. This convention is used to create Zeta specific event.
     *                       The wrapper pattern is used here to provide platform specific implementation.
     *                       In this example the one returned would be new ForgeZRegister(ZRegister event)
     *                       If no wrapper constructor is found and the provided class implements Event already returns null. Exception otherwise.
     */
    private <Z2 extends Z, F2 extends F> Function<Z2, F2> findZetaWrapper(Class<Z2> zetaEventClass) {
        // Find the constructor that takes a single parameter of type A
        for (Constructor<?> constructor : zetaEventClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && zetaEventRoot.isAssignableFrom(parameterTypes[0])) {
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
            if (!zetaToForgeEventClassHack.containsKey(baseZetaEventClass)) {
                zetaToForgeEventClassHack.put(baseZetaEventClass, eventField.getType());
            }
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
            if (!zetaToForgeEventClassHack.containsKey(baseZetaEventClass)) {
                zetaToForgeEventClassHack.put(baseZetaEventClass, zetaEventClass);
            }
            return null;
        }

        throw new RuntimeException("No wrapped forge Event found for Zeta event class " + zetaEventClass);
    }

    private <Z2 extends Z, F2 extends F> Function<F2, Z2> findWrappedZetaEvent(Class<Z2> zetaEventClass, Class<? extends Z> baseZetaEventClass) {
        Field eventField = findFieldInClassHierarchy(zetaEventClass, f -> zetaEventRoot.isAssignableFrom(f.getType()));
        if (eventField != null) {
            //hack
            eventField.setAccessible(true);
            return instance -> {
                try {
                    return (Z2) eventField.get(instance);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }

        throw new RuntimeException("No wrapped Zeta Event found for Zeta event class " + zetaEventClass);
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

    protected void registerListenerToForgeWithPriorityAndGenerics(IEventBus bus, Class<?> owningClazz, Consumer<? extends Event> consumer, Class<?> zetaEventClass) {
        EventPriority priority = guessPriorityFromClassName(owningClazz);
        Class<?> gen = generics.get(zetaEventClass);
        Class eventType = zetaToForgeEventClassHack.get(zetaEventClass);
        if (eventType == null) {
            throw new RuntimeException("No event type found for " + zetaEventClass);
        }
        if (gen != null) {
            bus.addGenericListener(gen, priority, false, eventType, (Consumer<GenericEvent>) consumer);
        } else {
            bus.addListener(priority, false, eventType, consumer);
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


    private static final Map<Class<?>, EventPriority> CACHE = new ConcurrentHashMap<>();

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

    public ForgeEventsRemapper<Z,F> makeCopy() {
        ForgeEventsRemapper<Z,F> copy = new ForgeEventsRemapper<>(zetaEventRoot, forgeEventRoot);
        copy.forgeToZetaMap.putAll(forgeToZetaMap);
        copy.zetaToForgeMap.putAll(zetaToForgeMap);
        copy.generics.putAll(generics);
        copy.zetaToForgeEventClassHack.putAll(zetaToForgeEventClassHack);
        return copy;
    }

}
