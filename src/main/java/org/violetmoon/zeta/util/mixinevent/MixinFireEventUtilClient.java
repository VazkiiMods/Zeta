package org.violetmoon.zeta.util.mixinevent;

import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Allows firing events via Mixins (Client sided now) :)
 * <p>
 * TODO: Would it be better to just... have a global list of ZetaClient instances, that can be queried for situations like this?
 */
public class MixinFireEventUtilClient {
    private static final Set<ZetaClient> interestedParties = new HashSet<>(2);

    public static void signup(ZetaClient z) {
        interestedParties.add(z);
    }




    public static <T extends IZetaPlayEvent> void fireEvent(T event) {
        interestedParties.forEach(z -> z.playBus.fire(event));
    }

    public static <T extends IZetaPlayEvent> void fireEvent(T event, Class<T> eventClass) {
        interestedParties.forEach(z -> z.playBus.fire(event, eventClass));
    }

    public static <T extends IZetaLoadEvent> void fireEvent(T event) {
        interestedParties.forEach(z -> z.loadBus.fire(event));
    }

    public static <T extends IZetaLoadEvent> void fireEvent(T event, Class<T> eventClass) {
        interestedParties.forEach(z -> z.loadBus.fire(event, eventClass));
    }
}
