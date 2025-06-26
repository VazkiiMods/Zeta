package org.violetmoon.zetaimplforge.event.play;


import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.violetmoon.zeta.event.play.ZLevelTick;

import net.minecraft.world.level.Level;

public abstract class ForgeZLevelTick implements ZLevelTick {
    public final LevelTickEvent e;

    public ForgeZLevelTick(LevelTickEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
    }

    public static class Start extends ForgeZLevelTick implements ZLevelTick.Start {
        public Start(LevelTickEvent.Pre e) {
            super(e);
        }
    }

    public static class End extends ForgeZLevelTick implements ZLevelTick.End {
        public End(LevelTickEvent.Post e) {
            super(e);
        }
    }
}
