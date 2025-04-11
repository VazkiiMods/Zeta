package org.violetmoon.zeta.client;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.client.event.play.ZRenderFrame;
import org.violetmoon.zeta.event.bus.PlayEvent;

//TODO: 1.21. replace with minecraft own ticker. Tbh this is legacy already and should be replaced with Minecraft.getPartialTicks()
@Deprecated
public final class ClientTicker {

    //no need to have more than 1 instance of this class. Ticks are always the same
    public static final ClientTicker INSTANCE = new ClientTicker();

    private ClientTicker() {
    }

    public float partialTicks = 0;
    public float delta = 0;
    public float total = 0;

    public int ticksInGame = 0;


    @PlayEvent
	public void onRenderTick(ZRenderFrame event) {
        partialTicks = Minecraft.getInstance().getTimer();
        delta = Minecraft.getInstance().getDeltaFrameTime();
        total = ticksInGame + partialTicks;
	}

    @ApiStatus.Internal
    @PlayEvent
    public void onEndClientTick(ZClientTick.End event) {
        if (!Minecraft.getInstance().isPaused()) {
            ticksInGame++;
        }
    }
}
