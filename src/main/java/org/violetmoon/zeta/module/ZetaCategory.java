package org.violetmoon.zeta.module;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.violetmoon.zeta.mod.ZetaMod;

/**
 * @see org.violetmoon.zeta.module.ZetaModuleManager for a good way to obtain these
 */
public class ZetaCategory {
	public final String name;
	public final Supplier<ItemStack> icon;
	public final @Nullable String requiredMod;
	private final boolean isRequiredModLoaded;

	public ZetaCategory(String name, Supplier<ItemStack> icon, @Nullable String requiredMod) {
		this.name = name;
		this.icon = icon;
		this.requiredMod = requiredMod;
		this.isRequiredModLoaded = requiredMod == null || ZetaMod.ZETA.isModLoaded(requiredMod);
	}

	public ZetaCategory(String name, Item icon, @Nullable String requiredMod) {
		this(name, () -> new ItemStack(icon), requiredMod);
	}

	public ZetaCategory(String name, Item icon) {
		this(name, icon, null);
	}

	public static ZetaCategory unknownCategory(String id) {
		return new ZetaCategory(id, () -> new ItemStack(Items.PAPER), null);
	}

	public boolean isAddon() {
		return requiredMod != null;
	}

	public boolean requiredModsLoaded() {
		return isRequiredModLoaded;
	}

	@Override
	public String toString() {
		return "ZetaCategory{" + name + "}";
	}

	//Intentionally does not override equals/hashcode (object identity comparison)

	// maybe this should be in a module class instead? is this supposed to be subclassed?
	public Component getDisabledTooltip() {
		return Component.translatable("zeta.misc.mod_disabled", requiredMod)
				.withStyle(ChatFormatting.GRAY);
	}
}
