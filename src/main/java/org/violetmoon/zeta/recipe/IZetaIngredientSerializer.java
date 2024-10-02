package org.violetmoon.zeta.recipe;

import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.violetmoon.zeta.Zeta;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

// Copy of Forge IIngredientSerializer
public interface IZetaIngredientSerializer<T extends ICustomIngredient> {
	T parse(FriendlyByteBuf buffer);

	T parse(JsonObject json);

	void write(FriendlyByteBuf buffer, T ingredient);

	//Aaaaaaaa
	Zeta getZeta();

	//TODO: Is getId needed?
	default ResourceLocation getID() {
		return getZeta().craftingExtensions.getID(this);
	}
}
