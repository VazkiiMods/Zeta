package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingConversion;

public abstract class ForgeZLivingConversion implements ZLivingConversion {
    private final LivingConversionEvent e;

    public ForgeZLivingConversion(LivingConversionEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    public static class Pre extends ForgeZLivingConversion implements ZLivingConversion.Pre {
        private final LivingConversionEvent.Pre e;

        public Pre(LivingConversionEvent.Pre e) {
            super(e);
            this.e = e;
        }

        @Override
        public EntityType<? extends LivingEntity> getOutcome() {
            return e.getOutcome();
        }

        @Override
        public boolean isCanceled() {
            return e.isCanceled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            e.setCanceled(cancel);
        }
    }

    public static class Post extends ForgeZLivingConversion implements ZLivingConversion.Post {
        private final LivingConversionEvent.Post e;

        public Post(LivingConversionEvent.Post e) {
            super(e);
            this.e = e;
        }

        @Override
        public LivingEntity getOutcome() {
            return e.getOutcome();
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public void setCanceled(boolean cancel) {
            // NO-OP
        }
    }
}
