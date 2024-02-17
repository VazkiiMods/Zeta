package org.violetmoon.zeta.item;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTabs;

public class ZetaSpawnEggItem extends SpawnEggItem implements IZetaItem {

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaSpawnEggItem(EntityType<? extends Mob> type, int primaryColor, int secondaryColor, String regname, @Nullable ZetaModule module, Item.Properties properties) {
		super(type, primaryColor, secondaryColor, properties);
		this.module = module;

		if(module == null) //auto registration below this line
			return;

		module.zeta.registry.registerItem(this, regname);
		CreativeTabManager.addToCreativeTab(CreativeModeTabs.SPAWN_EGGS, this);
	}

	@Override
	public ZetaSpawnEggItem setCondition(BooleanSupplier enabledSupplier) {
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