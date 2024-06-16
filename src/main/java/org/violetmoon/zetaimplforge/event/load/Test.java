package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.ZetaEventBus;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// This made sense when i wrote it. Now i have no clue
// Please somebody finish this
public class Test {

    private static final Map<Class<? extends IZetaLoadEvent>, Function<? extends Event, ? extends IZetaLoadEvent>> FORGE_TO_ZETA = Map.of(
            ForgeZAddReloadListener.class, (Function<AddReloadListenerEvent, IZetaLoadEvent>) ForgeZAddReloadListener::new
    );

    public static <T extends IZetaLoadEvent> Consumer<? extends Event> remap(Consumer<T> zetaEventConsumer, Class<T> cl) {
        Function<? extends Event, T> forgeToZeta = (Function<? extends Event, T>) FORGE_TO_ZETA.get(cl);
        return getEventConsumer(zetaEventConsumer, forgeToZeta);
    }

    @NotNull
    private static <T extends IZetaLoadEvent, E extends Event> Consumer<E> getEventConsumer(Consumer<T> zetaEventConsumer, Function<E, T> forgeToZeta) {
        return event -> zetaEventConsumer.accept(forgeToZeta.apply(event));
    }


    public static class ExampleZetaBus{
        private EventBus forgeBus;

        public <T extends IZetaLoadEvent> void addListener(Consumer<T> zetaEventConsumer, Class<T> cl){
            forgeBus.addListener(remap(zetaEventConsumer, cl));
        }

    }

}
