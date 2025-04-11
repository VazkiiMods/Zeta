package org.violetmoon.zeta.item;

import java.util.List;
import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.item.ext.IZetaItemExtensions;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

public class ZetaSmithingTemplateItem extends SmithingTemplateItem implements IZetaItem, IZetaItemExtensions {
	
	protected static final ChatFormatting Z_TITLE_FORMAT = ChatFormatting.GRAY;
	protected static final ChatFormatting Z_DESCRIPTION_FORMAT = ChatFormatting.BLUE;
	protected static final ResourceLocation Z_EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
	protected static final ResourceLocation Z_EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
	protected static final ResourceLocation Z_EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
	protected static final ResourceLocation Z_EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
	protected static final ResourceLocation Z_EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
	protected static final ResourceLocation Z_EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("item/empty_slot_axe");
	protected static final ResourceLocation Z_EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");
	protected static final ResourceLocation Z_EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace("item/empty_slot_shovel");
	protected static final ResourceLocation Z_EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe");
	protected static final ResourceLocation Z_EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("item/empty_slot_ingot");
	protected static final ResourceLocation Z_EMPTY_SLOT_REDSTONE_DUST = ResourceLocation.withDefaultNamespace("item/empty_slot_redstone_dust");
	protected static final ResourceLocation Z_EMPTY_SLOT_QUARTZ = ResourceLocation.withDefaultNamespace("item/empty_slot_quartz");
	protected static final ResourceLocation Z_EMPTY_SLOT_EMERALD = ResourceLocation.withDefaultNamespace("item/empty_slot_emerald");
	protected static final ResourceLocation Z_EMPTY_SLOT_DIAMOND = ResourceLocation.withDefaultNamespace("item/empty_slot_diamond");
	protected static final ResourceLocation Z_EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
	protected static final ResourceLocation Z_EMPTY_SLOT_AMETHYST_SHARD = ResourceLocation.withDefaultNamespace("item/empty_slot_amethyst_shard");

	protected static List<ResourceLocation> anyToolIconList() {
		return List.of(Z_EMPTY_SLOT_HELMET, Z_EMPTY_SLOT_SWORD, Z_EMPTY_SLOT_CHESTPLATE, Z_EMPTY_SLOT_PICKAXE, Z_EMPTY_SLOT_LEGGINGS, Z_EMPTY_SLOT_AXE, Z_EMPTY_SLOT_BOOTS, Z_EMPTY_SLOT_HOE, Z_EMPTY_SLOT_SHOVEL);
	}

	private final @Nullable ZetaModule module;
	private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

	public ZetaSmithingTemplateItem(String regname, @Nullable ZetaModule module,
									Component appliesTo,
									Component ingredients,
									Component upgradeDescription,
									Component baseSlotDescription,
									Component additionsSlotDescription,
									List<ResourceLocation> baseSlotEmptyIcons,
									List<ResourceLocation> additionalSlotEmptyIcons) {
		super(appliesTo, ingredients, upgradeDescription, baseSlotDescription, additionsSlotDescription, baseSlotEmptyIcons, additionalSlotEmptyIcons);

		this.module = module;
		if (module == null) //auto registration below this line
			return;

		module.zeta().registry.registerItem(this, regname);
	}

	@Override
	public ZetaSmithingTemplateItem setCondition(BooleanSupplier enabledSupplier) {
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
