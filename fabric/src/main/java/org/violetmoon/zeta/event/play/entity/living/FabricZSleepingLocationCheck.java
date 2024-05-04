package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import org.violetmoon.zeta.FabricZeta;
import org.violetmoon.zeta.event.bus.ZResult;

public class FabricZSleepingLocationCheck implements ZSleepingLocationCheck {
    private final SleepingLocationCheckEvent e;

    public FabricZSleepingLocationCheck(SleepingLocationCheckEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }


    @Override
    public BlockPos getSleepingLocation() {
        return e.getSleepingLocation();
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
