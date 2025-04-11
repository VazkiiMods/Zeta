package org.violetmoon.zeta.event.bus;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientTicker;
import org.violetmoon.zeta.mod.ZetaMod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class ZetaEventBus<E> {

    protected final Class<? extends Annotation> subscriberAnnotation;
    protected final Class<E> eventRoot;
    protected final @Nullable Logger logSpam;
    //each bus belongs to a specific zeta. Internally they can however delegate to an internal shared data structure such as to the forge event bus
    protected final Zeta ofZeta;

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    public ZetaEventBus(Class<? extends Annotation> subscriberAnnotation, Class<E> eventRoot,
                        @Nullable Logger logSpam, Zeta ofZeta) {
        Preconditions.checkArgument(eventRoot.isInterface(), "Event roots should be an interface");

        this.subscriberAnnotation = subscriberAnnotation;
        this.eventRoot = eventRoot;
        this.logSpam = logSpam;
        this.ofZeta = ofZeta;
    }

    /**
     * If the parameter is a Class: subscribes all static methods from it (and its superclasses) to the event bus.
     * Otherwise, subscribes all non-static methods on that object (and its superclasses) to the event bus.
     * (Note that the event bus will hold a reference to this object.)
     */
    public ZetaEventBus<E> subscribe(@NotNull Object target) {
        Preconditions.checkNotNull(target, "null passed to subscribe");

        Object receiver;
        Class<?> owningClazz;
        if (target instanceof Class<?> clazz) {
            receiver = null;
            owningClazz = clazz;
        } else {
            receiver = target;
            owningClazz = target.getClass();
        }
        streamAnnotatedMethods(owningClazz, receiver == null)
                .forEach(m -> subscribeMethod(m, receiver, owningClazz));
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
        if (target instanceof Class<?> clazz) {
            receiver = null;
            owningClazz = clazz;
        } else {
            receiver = target;
            owningClazz = target.getClass();
        }

        streamAnnotatedMethods(owningClazz, receiver == null)
                .forEach(m -> unsubscribeMethod(m, receiver, owningClazz));
        return this;
    }

    protected abstract void unsubscribeMethod(Method m, Object receiver, Class<?> owningClazz);

    protected abstract void subscribeMethod(Method m, Object receiver, Class<?> owningClazz);


    /**
     * Fires an event on the event bus. Each subscriber will be visited in order.
     */
    public abstract <T extends E> T fire(@NotNull T event);

    /**
     * Fires an event on the event bus. Each subscriber will be visited in order.
     * Listeners for "firedAs" will be invoked, instead of listeners for the event's own class.
     * <p>
     * (The generic should be Class&lt;? super T & ? extends E&gt;, but unfortunately, javac.)
     */
    public abstract <T extends E> T fire(@NotNull T event, Class<? super T> firedAs);

    /**
     * Grabs methods from this class (and its superclasses, recursively) that are annotated with this bus's
     * annotation; and of the requested staticness.
     */
    private Stream<Method> streamAnnotatedMethods(Class<?> owningClazz, boolean wantStatic) {
        Stream<Method> methods;
        if (ofZeta.isProduction) {
            // faster
            methods = Arrays.stream(owningClazz.getMethods());
        } else {
            // here for debug purposes as this will catch private stuff too
            List<Method> list = new ArrayList<>();
            while (owningClazz != null) {
                Collections.addAll(list, owningClazz.getDeclaredMethods());
                owningClazz = owningClazz.getSuperclass();
            }
            methods = list.stream();
        }
        return methods.filter(m -> m.isAnnotationPresent(subscriberAnnotation) && ((m.getModifiers() & Modifier.STATIC) != 0) == wantStatic);
    }


    protected RuntimeException arityERR(Method method) {
        return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
                " should take 1 parameter.", method, null);
    }

    protected RuntimeException typeERR(Method method) {
        return methodProblem("Method annotated with @" + subscriberAnnotation.getSimpleName() +
                " should take an implementor of " + eventRoot.getSimpleName() + ".", method, null);
    }

    protected RuntimeException unreflectERR(Method method, Throwable cause) {
        return methodProblem("Exception unreflecting a @" + subscriberAnnotation.getSimpleName() +
                " method, is it public?", method, cause);
    }

    protected static RuntimeException methodProblem(String problem, Method method, @Nullable Throwable cause) {
        return new RuntimeException("%s%nMethod class: %s%nMethod name: %s".formatted(
                problem, method.getDeclaringClass().getName(), method.getName()), cause);
    }

    //TODO or remove entirely. Platform specific behavior should be in fire implementation on each platform bus
    @Deprecated(forRemoval = true)
    public <T extends E> T fireExternal(@NotNull T event, Class<? super T> firedAs) {

        return event;
    }
}
