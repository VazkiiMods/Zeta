package org.violetmoon.zeta.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.violetmoon.zeta.Zeta;

import java.util.stream.Stream;

// Copy of Neoforge ICustomIngredient
public interface IZetaCustomIngredient extends ICustomIngredient { // TODO: Abstract later, use NF-provided CustomIngredient for now
	boolean test(ItemStack var1);

	Stream<ItemStack> getItems();

	boolean isSimple();

	IngredientType<?> getType();

	Zeta getZeta();
}
