package org.violetmoon.zeta.event.bus.wip;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForgeZetaBus<E> extends ZetaBus<E> {

    private final Map<Class<? extends E>, Function<? extends Event, ? extends E>> forgeToZetaMap = new HashMap<>();
    private final Map<Class<? extends Event>, Function<? extends E, ? extends Event>> zetaToForgeMap = new HashMap<>();
    //ForgeZAddReloadListener.class, (Function<AddReloadListenerEvent, IZetaLoadEvent>) ForgeZAddReloadListener::new

    private final IEventBus forgeBus;

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    public ForgeZetaBus(Zeta z, Class<? extends Annotation> subscriberAnnotation, Class<E> eventRoot, @Nullable Logger logSpam) {
        super(z, subscriberAnnotation, eventRoot, logSpam);
        this.forgeBus = MinecraftForge.EVENT_BUS;
    }

    public void registerEventMappings(Class<? extends E> zetaEvent,
                                      Function<? extends Event, ? extends E> forgeToZeta,
                                      Class<? extends Event> forgeEvent,
                                      Function<? extends E, ? extends Event> zetaToForge) {
        forgeToZetaMap.put(zetaEvent, forgeToZeta);
        zetaToForgeMap.put(forgeEvent, zetaToForge);
    }

    // takes a method that takes a zeta event and turns into one that takes a forge event
    private Consumer<? extends Event> remapMethod(MethodHandle zetaEventConsumer, Class<? extends E> zetaEventClass) {
        Function<? extends Event, ? extends E> forgeToZetaFunc = forgeToZetaMap.get(zetaEventClass);
        return createForgeConsumer(zetaEventConsumer, forgeToZetaFunc);
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

    @Override
    protected void addListener(Method method, Object receiver, Class<?> owningClazz) {
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

        forgeBus.addListener(remapMethod(handle, (Class<? extends E>) eventType));
    }

    @Override
    protected void removeListener(Method m, Object receiver, Class<?> owningClazz) {

    }


    @Override
    public <T extends E> T fire(@NotNull T event) {
        forgeBus.post(remapEvent(event));
        return event;
    }

    private <T extends E> Event remapEvent(@NotNull T event) {
        Function<? extends E, ? extends Event> zetaToForgeFunc = zetaToForgeMap.get(event.getClass());
        return createForgeEvent(event, zetaToForgeFunc);
    }

    private <T extends E> Event createForgeEvent(@NotNull E event, Function<T, ? extends Event> function) {
        return function.apply((T) event);
    }

    @Override
    public <T extends E> T fire(@NotNull T event, Class<? extends T> firedAs) {
        return null;
    }
}
