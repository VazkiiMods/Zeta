package org.violetmoon.zeta.event.play.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import org.violetmoon.zeta.FabricZeta;
import org.violetmoon.zeta.event.bus.ZResult;

public class FabricZEntityMobGriefing implements ZEntityMobGriefing {
    private final EntityMobGriefingEvent e;

    public FabricZEntityMobGriefing(EntityMobGriefingEvent e) {
        this.e = e;
    }

    @Override
    public Entity getEntity() {
        return e.getEntity();
    }

    @Override
    public ZResult getResult() {
        return FabricZeta.from(e.getResult());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(FabricZeta.to(value));
    }
}
