package org.violetmoon.zeta.event.play;

import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.event.play.furnace.ZFurnaceFuelBurnTime;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

public class ForgeZFurnaceFuelBurnTime implements ZFurnaceFuelBurnTime {
	private final FurnaceFuelBurnTimeEvent e;

	public ForgeZFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent e) {
		this.e = e;
	}

	@Override
	public @NotNull ItemStack getItemStack() {
		return e.getItemStack();
	}

	@Override
	public @Nullable RecipeType<?> getRecipeType() {
		return e.getRecipeType();
	}

	@Override
	public void setBurnTime(int burnTime) {
		e.setBurnTime(burnTime);
	}

	@Override
	public int getBurnTime() {
		return e.getBurnTime();
	}
}
