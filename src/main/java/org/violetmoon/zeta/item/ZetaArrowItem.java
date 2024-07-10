package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ZetaArrowItem extends ArrowItem implements IZetaItem {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaArrowItem(String name, @Nullable ZetaModule module) {
		super(new Item.Properties());

		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerItem(this, name);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.COMBAT, this);
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

		private final ArrowCreator creator;
		
		public Impl(String name, ZetaModule module, ArrowCreator creator) {
			super(name, module);
			this.creator = creator;
		}
		
		@Override
		public AbstractArrow createArrow(Level level, ItemStack arrowStack, LivingEntity entity, @javax.annotation.Nullable ItemStack weaponStack) {
			return creator.createArrow(level, arrowStack, entity, weaponStack);
		}
		
		public interface ArrowCreator {
			AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity living, @javax.annotation.Nullable ItemStack weaponStack);
		}
		
	}
	
}
