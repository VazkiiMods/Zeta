package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class FabricZLivingDeath implements ZLivingDeath {
	private final LivingDeathEvent e;

	public FabricZLivingDeath(LivingDeathEvent e) {
		this.e = e;
	}

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}

	@Override
	public DamageSource getSource() {
		return e.getSource();
	}

	public static class Lowest extends FabricZLivingDeath implements ZLivingDeath.Lowest {
		public Lowest(LivingDeathEvent e) {
			super(e);
		}
	}
}
