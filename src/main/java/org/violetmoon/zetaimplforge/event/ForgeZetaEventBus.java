package org.violetmoon.zetaimplforge.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.*;
import org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags;
import org.violetmoon.zetaimplforge.mod.ZetaModForge;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

// this is super jank. Basically converts all zeta events to forge ones, then delegates to the forge bus directly
public class ForgeZetaEventBus<Z, F extends Event> extends ZetaEventBus<Z> {

    // needed so we can unregister later
    private final Map<ForgeZetaEventBus.Key, Object> convertedHandlers = new Object2ObjectOpenHashMap<>();

    private final IEventBus forgeBus;
    private final Class<F> forgeEventRoot; //probably not needed can be replaced with Event
    private final ForgeEventsRemapper<Z, F> remapper;

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    protected ForgeZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<Z> eventRoot,
                                @Nullable Logger logSpam, IEventBus forgeBus, Class<F> forgeEventRoot,
                                Zeta ofZeta, ForgeEventsRemapper<Z, F> remapper) {
        super(subscriberAnnotation, eventRoot, logSpam, ofZeta);
        this.forgeBus = forgeBus;
        this.forgeEventRoot = forgeEventRoot;
        this.remapper = remapper;
    }

    public static ForgeZetaEventBus<IZetaLoadEvent, Event> ofLoadBus(@Nullable Logger logSpam, Zeta ofZeta) {
        return new ForgeZetaEventBus<>(
                LoadEvent.class, IZetaLoadEvent.class,
                logSpam, FMLJavaModLoadingContext.get().getModEventBus(), Event.class,
                ofZeta, DEFAULT_LOAD_EVENTS_REMAPPER);
    }

    public static ForgeZetaEventBus<IZetaPlayEvent, Event> ofPlayBus(@Nullable Logger logSpam, Zeta ofZeta) {
        return new ForgeZetaEventBus<>(
                PlayEvent.class, IZetaPlayEvent.class,
                logSpam, MinecraftForge.EVENT_BUS, Event.class,
                ofZeta, DEFAULT_PLAY_EVENTS_REMAPPER);
    }


    @Override
    protected void subscribeMethod(Method method, Object receiver, Class<?> owningClazz) {
        if (method.getParameterCount() != 1)
            throw arityERR(method);

        Class<?> zetaEventClass = method.getParameterTypes()[0];

        //check if it's already a forge event, or it's a zeta event
        if (!eventRoot.isAssignableFrom(zetaEventClass) && !forgeEventRoot.isAssignableFrom(zetaEventClass)) {
            throw typeERR(method);
        }

        MethodHandle handle;
        try {
            handle = MethodHandles.publicLookup().unreflect(method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //fill in the "this" parameter
        if (receiver != null)
            handle = handle.bindTo(receiver);

        Object convertedMethod = remapper.remapAndRegister(forgeBus, owningClazz, handle, zetaEventClass);
        //store here so we can unregister later
        convertedHandlers.put(new Key(method, receiver, owningClazz), convertedMethod);
    }

    @Override
    protected void unsubscribeMethod(Method m, Object receiver, Class<?> owningClazz) {
        var handler = convertedHandlers.remove(new Key(m, receiver, owningClazz));
        if (handler != null) {
            synchronized (forgeBus) {
                forgeBus.unregister(handler);
            }
        }
    }

    private record Key(Method method, Object receiver, Class<?> owningClazz) {
    }

    @Override
    public <T extends Z> T fire(@NotNull T event) {
        forgeBus.post(remapper.remapEvent(event, event.getClass()));
        return event;
    }

    @Override
    public <T extends Z> T fire(@NotNull T event, Class<? super T> firedAs) {
        forgeBus.post(remapper.remapEvent(event, firedAs));
        return event;
    }

    // all these are for bus specific events. The only bus that should have specific stuff is the play one

    // for generic events
    public <S extends Z, C extends Z> void registerWrapperWithGenerics(Class<C> baseZetaEventClass, Class<S> forgeZetaEventClass, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerWrapperWithGeneric(baseZetaEventClass, forgeZetaEventClass, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass) {
        synchronized (remapper) {
            remapper.registerWrapper(baseZetaEventClass, forgeZetaEventClass, null);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapperWithGenerics(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                                         Function<? extends F, ZF> constructor, Class<?> genericClass) {
        synchronized (remapper) {
            remapper.registerWrapperWithGeneric(baseZetaEventClass, forgeZetaEventClass, constructor, genericClass);
        }
    }

    public <ZF extends Z, ZB extends Z> void registerWrapper(Class<ZB> baseZetaEventClass, Class<ZF> forgeZetaEventClass,
                                                             @Nullable Function<? extends F, ZF> constructor) {
        synchronized (remapper) {
            remapper.registerWrapper(baseZetaEventClass, forgeZetaEventClass, constructor);
        }
    }

    public <ZF extends Z, ZB extends Z, F2 extends F> void registerWrapper(Class<ZB> baseZetaEventClass, Class<F2> forgeEventClass,
                                                                           Function<F2, ZF> constructor, Function<ZF, ? extends F> unwrapper) {
        synchronized (remapper) {
            remapper.registerWrapper(baseZetaEventClass, forgeEventClass, constructor, unwrapper);
        }
    }

    public <ZF extends Z, ZB extends Z, F2 extends F> void registerWrapperWithGenerics(Class<ZB> baseZetaEventClass, Class<F2> forgeEventClass,
                                                                                       Function<F2, ZF> constructor, Function<ZF, ? extends F> unwrapper, Class<?> generic) {
        synchronized (remapper) {
            remapper.registerWrapperWithGenerics(baseZetaEventClass, forgeEventClass, constructor, unwrapper, generic);
        }
    }

    // I would love to put this code in the mod proxy but that needs to do event setup stuff which requires these busses to be fully initialized

    // instances so we don't create multiple as reflections take time and memory
    private static final ForgeEventsRemapper<IZetaLoadEvent, Event> DEFAULT_LOAD_EVENTS_REMAPPER = Util.make(
            new ForgeEventsRemapper<>(IZetaLoadEvent.class, Event.class), ZetaModForge.PROXY::addKnownZetaLoadEvents
    );

    private static final ForgeEventsRemapper<IZetaPlayEvent, Event> DEFAULT_PLAY_EVENTS_REMAPPER = Util.make(
            new ForgeEventsRemapper<>(IZetaPlayEvent.class, Event.class), ZetaModForge.PROXY::addKnownZetaPlayEvents
    );


}
