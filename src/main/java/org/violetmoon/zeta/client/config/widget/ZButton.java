package org.violetmoon.zeta.client.config.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.client.config.screen.ZetaConfigHomeScreen;
import org.violetmoon.zetaimplforge.mod.ZetaModClientForge;
import org.violetmoon.zetaimplforge.mod.ZetaModClientProxy;

import java.io.File;

public class ZButton extends Button {
    private boolean showBubble;

    public ZButton(int x, int y) {
        super(Button.builder(Component.literal("z"), ZButton::click).size(20, 20).pos(x, y));
    }

    @Override
    public int getFGColor() {
        return 0x48DDBC;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private static File getQuarkMarkerFile() {
        return new File(Minecraft.getInstance().gameDirectory, ".zmenu_opened.marker");
    }

    public static void click(Button b) {
        Minecraft.getInstance().setScreen(new ZetaConfigHomeScreen(ZetaModClientForge.ZETA_CLIENT,Minecraft.getInstance().screen));
    }

    private record Celebration(int day, int month, int len, int tier, String name) {

        // AFAIK none of the ones I'm tracking pass beyond a month so this
        // lazy check is fine
        public boolean running(int day, int month) {
            return this.month == month && (this.day >= day && this.day <= (day + len));
        }
    }

}
