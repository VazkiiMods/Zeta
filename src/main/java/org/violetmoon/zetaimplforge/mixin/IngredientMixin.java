package org.violetmoon.zetaimplforge.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.zeta.recipe.IZetaIngredient;
import org.violetmoon.zeta.recipe.IZetaIngredientSerializer;
import org.violetmoon.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;

/**
 * Forge hacks an "IIngredientSerializer getSerializer()" method onto Ingredient, which Zeta's ingredients
 * can't directly implement since IIngredientSerializer is a Forge-only class.
 */
@Mixin(Ingredient.class)
public class IngredientMixin {
	@Inject(method = "getSerializer", remap = false, at = @At("HEAD"), cancellable = true)
	public void blah(CallbackInfoReturnable<IIngredientSerializer<? extends Ingredient>> cir) {
		Ingredient self = (Ingredient) (Object) this;

		if(self instanceof IZetaIngredient<?> zIng) {
			IZetaIngredientSerializer<?> ser = zIng.zetaGetSerializer();

			IIngredientSerializer<? extends Ingredient> forge = ((ForgeCraftingExtensionsRegistry) ser.getZeta().craftingExtensions)
				.toForgeIngredientSerializers.get(ser);

			if(forge != null)
				cir.setReturnValue(forge);
		}
	}
}
