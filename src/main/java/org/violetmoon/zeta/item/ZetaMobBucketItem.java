package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluid;

public class ZetaMobBucketItem extends MobBucketItem implements IZetaItem {

	private final @Nullable ZetaModule module;

	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaMobBucketItem(EntityType<?> entity, Fluid fluid, SoundEvent sound, String name, @Nullable ZetaModule module) {
		super(entity, fluid, sound, (new Properties()).stacksTo(1));

		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta().registry.registerItem(this, name);
		CreativeTabManager.addNextToItem(CreativeModeTabs.TOOLS_AND_UTILITIES, this, Items.AXOLOTL_BUCKET, false);
	}

	@Override
	public ZetaMobBucketItem setCondition(BooleanSupplier enabledSupplier) {
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

}
