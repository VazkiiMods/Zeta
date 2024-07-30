package org.violetmoon.zeta.event.bus.wip;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class ZetaBus<E> {

    protected final Class<? extends Annotation> subscriberAnnotation;
    protected final Class<E> eventRoot;
    protected final Zeta z;
    protected final @Nullable Logger logSpam;

    /**
     * @param subscriberAnnotation The annotation that subscribe()/unsubscribe() will pay attention to.
     * @param eventRoot            The superinterface of all events fired on this bus.
     */
    public ZetaBus(Zeta z, Class<? extends Annotation> subscriberAnnotation, Class<E> eventRoot, @Nullable Logger logSpam) {
        Preconditions.checkArgument(eventRoot.isInterface(), "Event roots should be an interface");

        this.z = z;
        this.subscriberAnnotation = subscriberAnnotation;
        this.eventRoot = eventRoot;
        this.logSpam = logSpam;
    }

    /**
     * If the parameter is a Class: subscribes all static methods from it (and its superclasses) to the event bus.
     * Otherwise, subscribes all non-static methods on that object (and its superclasses) to the event bus.
     * (Note that the event bus will hold a reference to this object.)
     */
    public ZetaBus<E> subscribe(@NotNull Object target) {
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
                .forEach(m -> addListener(m, receiver, owningClazz));
        return this;
    }

    protected abstract void addListener(Method m, Object receiver, Class<?> owningClazz);

    /**
     * If the parameter is a Class: unsubscribes all static methods from it (and its superclasses) from the event bus.
     * Otherwise, unsubscribes all non-static methods on that object (and its superclasses) from the event bus.
     */
    public ZetaBus<E> unsubscribe(@NotNull Object target) {
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
                .forEach(m -> unregisterMethod(m, receiver, owningClazz));
        return this;
    }

    protected abstract void unregisterMethod(Method m, Object receiver, Class<?> owningClazz);

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
    public abstract <T extends E> T fire(@NotNull T event, Class<? extends T> firedAs);

    /**
     * Grabs methods from this class (and its superclasses, recursively) that are annotated with this bus's
     * annotation; and of the requested staticness.
     */
    private Stream<Method> streamAnnotatedMethods(Class<?> owningClazz, boolean wantStatic) {
        return Arrays.stream(owningClazz.getMethods())
                .filter(m -> m.isAnnotationPresent(subscriberAnnotation) && ((m.getModifiers() & Modifier.STATIC) != 0) == wantStatic);
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
}
