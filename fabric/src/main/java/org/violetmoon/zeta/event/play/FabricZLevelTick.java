package org.violetmoon.zeta.event.play;


import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public class FabricZLevelTick implements ZLevelTick {
    private final TickEvent.LevelTickEvent e;

    public FabricZLevelTick(TickEvent.LevelTickEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.level;
    }

    public static class Start extends FabricZLevelTick implements ZLevelTick.Start {
        public Start(TickEvent.LevelTickEvent e) {
            super(e);
        }
    }

    public static class End extends FabricZLevelTick implements ZLevelTick.End {
        public End(TickEvent.LevelTickEvent e) {
            super(e);
        }
    }
}
