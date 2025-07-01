package org.violetmoon.zeta.client.config.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.client.event.play.ZScreen;
import org.violetmoon.zeta.event.bus.PlayEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ZButtonHandler {
    @PlayEvent
    public static void onGuiInit(ZScreen.Init.Post event) {
        Set<String> targetButtonTranslationKeys = getTargetButtons(event.getScreen());
        if(targetButtonTranslationKeys == null || targetButtonTranslationKeys.isEmpty())
            return;

        Set<String> targetButtonNames = targetButtonTranslationKeys.stream()
                .map(I18n::get)
                .collect(Collectors.toSet());

        List<GuiEventListener> listeners = event.getListenersList();
        for(GuiEventListener listener : listeners)
            if(listener instanceof AbstractWidget widget) {
                if(targetButtonNames.contains(widget.getMessage().getString())) {
                    int x = widget.getX() + (widget.getWidth() + 4);

                    Button zButton = new ZButton(x, widget.getY());
                    event.addListener(zButton);
                    return;
                }
            }
    }

    private static @Nullable Set<String> getTargetButtons(Screen gui) {
        if(gui instanceof TitleScreen)
            return Set.of("fml.menu.mods.title", "fml.menu.mods"); // Mods (idk which one is used)

        if(gui instanceof PauseScreen)
            return Set.of("menu.options"); // Options...
        return null;
    }
}
