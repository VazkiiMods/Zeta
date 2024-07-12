package org.violetmoon.zetaimplforge.event.play;


import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.violetmoon.zeta.event.play.ZLevelTick;

import net.minecraft.world.level.Level;

public class ForgeZLevelTick implements ZLevelTick {
    private final LevelTickEvent e;

    public ForgeZLevelTick(LevelTickEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
    }

    public static class Start extends ForgeZLevelTick implements ZLevelTick.Start {
        public Start(LevelTickEvent e) {
            super(e);
        }
    }

    public static class End extends ForgeZLevelTick implements ZLevelTick.End {
        public End(LevelTickEvent e) {
            super(e);
        }
    }
}
