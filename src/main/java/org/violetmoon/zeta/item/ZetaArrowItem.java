package org.violetmoon.zeta.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public abstract class ZetaArrowItem extends ArrowItem implements IZetaItem {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaArrowItem(String name, @Nullable ZetaModule module) {
		super(new Item.Properties());

		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta().registry.registerItem(this, name);
		CreativeTabManager.addToTab(CreativeModeTabs.COMBAT, this);
	}

	@Override
	public ZetaArrowItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Nullable
	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}
	
	public static class Impl extends ZetaArrowItem {

		private final ArrowItemCreator itemCreator;
		private final ArrowProjectileCreator projectileCreator;

		public Impl(String name, ZetaModule module, ArrowItemCreator itemCreator, ArrowProjectileCreator projectileCreator) {
			super(name, module);
			this.itemCreator = itemCreator;
			this.projectileCreator = projectileCreator;
		}

		@Override
		public AbstractArrow createArrow(Level level, ItemStack pickupStack, LivingEntity shooter, @Nullable ItemStack weapon) {
			return itemCreator.createArrow(level, shooter, pickupStack, weapon);
		}

		@Override
		public Projectile asProjectile(Level level, Position position, @Nullable ItemStack pickupStack, Direction direction) {
			return projectileCreator.createProjectile(level, position.x(), position.y(), position.z(), pickupStack, null);
		}

		public interface ArrowItemCreator {
			AbstractArrow createArrow(Level level, LivingEntity shooter, ItemStack pickupStack, @Nullable ItemStack weapon);
		}

		public interface ArrowProjectileCreator {
			Projectile createProjectile(Level level, double x, double y, double z, ItemStack pickupStack, @Nullable ItemStack weapon);
		}
	}
}
