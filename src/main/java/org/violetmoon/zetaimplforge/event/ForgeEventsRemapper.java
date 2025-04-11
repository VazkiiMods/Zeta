package org.violetmoon.zetaimplforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.mod.ZetaMod;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

//@SuppressWarnings({"unchecked","rawtypes"})
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
    protected <Z2 extends Z> F remapEvent(@NotNull Z2 zetaEvent, Class<?> firedAs) {
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
    @Nullable
    protected Consumer<? extends F> remapMethod(MethodHandle originalEventConsumer, Class<?> zetaEventBaseClass, Class<?> forgeEventClass) {
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
            } else {
                return null;
            }
        }
        return createForgeConsumer(originalEventConsumer, forgeToZetaFunc, zetaEventBaseClass, forgeEventClass);
    }


    // generic bs
    private <T extends Z> F createForgeEvent(@NotNull Z event, Function<T, ? extends F> function) {
        return function.apply((T) event);
    }

    // generic bs
    // Creates the Forge Event Consumer that will be registered with Forge Event Bus
    private <F2 extends F, Z2 extends Z> Consumer<F2> createForgeConsumer(
            MethodHandle zetaEventConsumer, Function<F2, Z2> forgeToZetaFunc, Class<?> zetaEventBaseClass, Class<?> forgeEventClass) {

        //special cases fo forge events that are to be sub divided into phases

        // for gui overlay event
        VanillaGuiOverlay overlay = guessGuiOverlayFromClassName(zetaEventBaseClass, forgeEventClass);
        if (overlay != null) {
            //here we know that phase must not be null
            return event -> {
                try {
                    RenderGuiOverlayEvent rge = (RenderGuiOverlayEvent) event;
                    if (rge.getOverlay() != overlay.type()) return;
                    zetaEventConsumer.invoke(forgeToZetaFunc.apply(event));
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }

        //hack for tick events
        Phase phase = Phase.guessFromClassName(zetaEventBaseClass, forgeEventClass);
        if (phase != Phase.NONE) {
            return event -> {
                try {
                    //luckily this phase madness will go away with new neoforge
                    TickEvent te = (TickEvent) event;
                    if ((phase == Phase.START) ^ (te.phase == TickEvent.Phase.START)) return;
                    zetaEventConsumer.invoke(forgeToZetaFunc.apply(event));
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }

        return event -> {
            try {
                zetaEventConsumer.invoke(forgeToZetaFunc.apply(event));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    // for generic events

    // auto register ones are deprecated. Register manually, its faster and requires no reflection hacks
    @Deprecated
    public <S extends Z, C extends Z> void registerWrapperWithGeneric(Class<C> baseZetaEventClass, Class<S> forgeZetaEventClass, Class<?> genericClass) {
        registerWrapper(baseZetaEventClass, forgeZetaEventClass);
        generics.put(baseZetaEventClass, genericClass);
    }

    @Deprecated
    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass) {

        registerWrapper(baseZetaEventClass, forgeZetaEventClass, null);
    }

    @Deprecated
    public <ZF extends Z, ZB extends Z> void registerWrapperWithGeneric(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                                        Function<? extends F, ZF> constructor, Class<?> genericClass) {
        registerWrapper(baseZetaEventClass, forgeZetaEventClass, constructor);
        generics.put(baseZetaEventClass, genericClass);
    }

    //register known ones
    public <ZF extends Z, ZB extends Z, F2 extends F> void registerWrapper(Class<ZB> baseZetaEventClass, Class<F2> forgeEventClass,
                                                                           Function<F2, ZF> constructor, Function<ZF, ? extends F> unwrapper) {
        zetaToForgeEventClassHack.put(baseZetaEventClass, forgeEventClass);
        forgeToZetaMap.put(baseZetaEventClass, constructor);
        zetaToForgeMap.put(baseZetaEventClass, unwrapper);
    }

    public <ZF extends Z, ZB extends Z, F2 extends F> void registerWrapperWithGenerics(Class<ZB> baseZetaEventClass, Class<F2> forgeEventClass,
                                                                                       Function<F2, ZF> constructor, Function<ZF, ? extends F> unwrapper,
                                                                                       Class<?> genericClass) {
        zetaToForgeEventClassHack.put(baseZetaEventClass, forgeEventClass);
        forgeToZetaMap.put(baseZetaEventClass, constructor);
        zetaToForgeMap.put(baseZetaEventClass, unwrapper);
        generics.put(baseZetaEventClass, genericClass);
    }


    @Deprecated
    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                             @Nullable Function<? extends F, ZF> constructor) {
        Object old1;
        Object old2 = null;
        boolean isNoWrapper = false;

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

        throw new RuntimeException("No e forge Event found for Zeta event class " + zetaEventClass);
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

        throw new RuntimeException("No e Zeta Event found for Zeta event class " + zetaEventClass);
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

    protected <F2 extends F> void registerListenerToForgeWithPriorityAndGenerics(
            IEventBus bus, Class<?> owningClazz, Consumer<F2> consumer, Class<?> zetaEventClass, Class forgeEventClass) {
        EventPriority priority = guessPriorityFromClassName(owningClazz);
        Class<?> gen = generics.get(zetaEventClass);
        if (forgeEventClass == null) {
            throw new RuntimeException("No event type found for " + zetaEventClass);
        }
        if (gen != null) {
            bus.addGenericListener(gen, priority, false, forgeEventClass, (Consumer<GenericEvent>) (Object) consumer);
        } else {
            bus.addListener(priority, false, forgeEventClass, consumer);
        }
    }

    public Object remapAndRegister(IEventBus forgeBus, Class<?> owningClazz, MethodHandle handle, Class<?> zetaEventClass) {
        Class<?> forgeEventClass = zetaToForgeEventClassHack.get(zetaEventClass);

        Consumer<? extends F> consumer = this.remapMethod(handle, zetaEventClass, forgeEventClass);

        if (consumer == null) {

            //check for client hack
            if (isClientEvent(zetaEventClass)) {
                if (ZetaMod.ZETA.isProduction) {
                    ZetaMod.LOGGER.error("Client event {} was found in a non client only class!", zetaEventClass);
                    return new Object();
                }
                throw new RuntimeException("Client event " + zetaEventClass + " was found in a non client only class!");
            }

            throw new RuntimeException("Could not convert Zeta event class " + zetaEventClass + " to Forge event " +
                    "(in class " + owningClazz + "). You must register its subclass using registerSubclass.");
        }

        registerListenerToForgeWithPriorityAndGenerics(forgeBus, owningClazz, consumer, zetaEventClass, forgeEventClass);

        return consumer;
    }

    //remove once all zeta client events are moved to client replacement module as they should
    @Deprecated(forRemoval = true)
    private boolean isClientEvent(Class<?> zetaEventClass) {
        String path = zetaEventClass.getPackageName();
        return path.startsWith("org.violetmoon.zeta.client.event");
    }


    private enum Phase {
        NONE, START, END;

        private static Phase guessFromClassName(Class<?> zetaEventClass, Class<?> forgeClass) {
            if (!TickEvent.class.isAssignableFrom(forgeClass)) return NONE;
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

    private static final Map<Class<?>, VanillaGuiOverlay> GUI_OVERLAY_CACHE = new ConcurrentHashMap<>();
    private static final Pattern INNER_CLASS_PATTERN = Pattern.compile("\\$([^$]+)\\$");

    @Nullable
    private static VanillaGuiOverlay guessGuiOverlayFromClassName(Class<?> zetaEventClass, Class<?> forgeEventClass) {
        if (!RenderGuiOverlayEvent.class.isAssignableFrom(forgeEventClass)) return null;
        return GUI_OVERLAY_CACHE.computeIfAbsent(zetaEventClass, zec -> {
            var match = INNER_CLASS_PATTERN.matcher(zetaEventClass.getName());
            if (!match.find()) return null;
            String simpleName = match.group(1);
            for (VanillaGuiOverlay overlay : VanillaGuiOverlay.values()) {
                if (simpleName.equalsIgnoreCase(overlay.name().replace("_", ""))) {
                    return overlay;
                }
            }
            return null;
        });
    }


    private static final Map<Class<?>, EventPriority> PRIORITY_CACHE = new ConcurrentHashMap<>();

    private static EventPriority guessPriorityFromClassName(Class<?> zetaEventClass) {
        return PRIORITY_CACHE.computeIfAbsent(zetaEventClass, zec -> {
            String simpleName = zec.getSimpleName();
            for (EventPriority p : EventPriority.values()) {
                String name = WordUtils.capitalizeFully(p.name().toLowerCase());
                if (simpleName.endsWith(name)) {
                    return p;
                }
            }
            return EventPriority.NORMAL;
        });
    }

    public ForgeEventsRemapper<Z, F> makeCopy() {
        ForgeEventsRemapper<Z, F> copy = new ForgeEventsRemapper<>(zetaEventRoot, forgeEventRoot);
        copy.forgeToZetaMap.putAll(forgeToZetaMap);
        copy.zetaToForgeMap.putAll(zetaToForgeMap);
        copy.generics.putAll(generics);
        copy.zetaToForgeEventClassHack.putAll(zetaToForgeEventClassHack);
        return copy;
    }

}
