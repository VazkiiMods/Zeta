package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.violetmoon.zeta.event.play.entity.living.ZLivingDrops;

import java.util.Collection;

public class ForgeZLivingDrops implements ZLivingDrops {
	public final LivingDropsEvent e;

	public ForgeZLivingDrops(LivingDropsEvent e) {
		this.e = e;
	}

	@Override
	public LivingEntity getEntity() {
		return e.getEntity();
	}

	@Override
	public DamageSource getSource() {
		return e.getSource();
	}

	@Override
	public Collection<ItemEntity> getDrops() {
		return e.getDrops();
	}

	@Override
	public boolean isRecentlyHit() {
		return e.isRecentlyHit();
	}

	@Override
	public boolean isCanceled() {
		return e.isCanceled();
	}

	@Override
	public void setCanceled(boolean cancel) {
		e.setCanceled(cancel);
	}

	public static class Lowest extends ForgeZLivingDrops implements ZLivingDrops.Lowest {
		public Lowest(LivingDropsEvent e) {
			super(e);
		}
	}
}
