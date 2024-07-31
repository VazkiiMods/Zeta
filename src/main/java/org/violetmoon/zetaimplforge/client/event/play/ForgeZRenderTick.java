package org.violetmoon.zetaimplforge.client.event.play;

import org.violetmoon.zeta.client.event.play.ZRenderTick;

import net.minecraftforge.event.TickEvent;

import java.util.Objects;

public class ForgeZRenderTick implements ZRenderTick {
    private final TickEvent.RenderTickEvent e;

    public ForgeZRenderTick(TickEvent.RenderTickEvent e) {
        this.e = e;
    }

    @Override
    public float getRenderTickTime() {
        return e.renderTickTime;
    }

	public static class Start extends ForgeZRenderTick implements ZRenderTick.Start {
		public Start(TickEvent.RenderTickEvent e) {
			super(e);
		}
	}

	public static class End extends ForgeZRenderTick implements ZRenderTick.End {
		public End(TickEvent.RenderTickEvent e) {
			super(e);
		}
	}

}
