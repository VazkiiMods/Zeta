package org.violetmoon.zeta.registry;

import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

import java.util.Map;

/**
 * @author WireSegal
 * Created at 3:34 PM on 9/23/19.
 */

//Todo: This NEEDS a rewrite, I feel its been butchered in 1.21
public abstract class BrewingRegistry {

	protected final Zeta zeta;

	public BrewingRegistry(Zeta zeta) {
		this.zeta = zeta;
	}

	public void addPotionMix(String flag, Item reagent, MobEffect effect) {
		addPotionMix(flag, reagent, effect, null);
	}

	public void addPotionMix(String flag, Item reagent, MobEffect effect, int normalTime, int longTime, int strongTime) {
		addPotionMix(flag, reagent, effect, null, normalTime, longTime, strongTime);
	}

	public void addPotionMix(String flag, Item reagent, MobEffect effect, @Nullable MobEffect negation) {
		addPotionMix(flag, reagent, effect, negation, 3600, 9600, 1800);
	}

	public void addPotionMix(String flag, Item reagent, MobEffect effect, @Nullable MobEffect negation, int normalTime, int longTime, int strongTime) {
		ResourceLocation loc = zeta.registry.getRegistryName(effect, BuiltInRegistries.MOB_EFFECT);

		if (loc != null) {
			String baseName = loc.getPath();
			boolean hasStrong = strongTime > 0;
			Holder<MobEffect> effectHolder = Holder.direct(effect);

			Potion normalType = registerPotion(new MobEffectInstance(effectHolder, normalTime), baseName, baseName);
			Potion longType = registerPotion(new MobEffectInstance(effectHolder, longTime), baseName, "long_" + baseName);
			Potion strongType = !hasStrong ? null : registerPotion(new MobEffectInstance(effectHolder, strongTime, 1), baseName, "strong_" + baseName);

			addPotionMix(flag, reagent, normalType, longType, strongType);

			if (negation != null) {
				ResourceLocation negationLoc = zeta.registry.getRegistryName(negation, BuiltInRegistries.MOB_EFFECT);
				if (negationLoc != null) {
					String negationBaseName = negationLoc.getPath();
					Holder<MobEffect> negationHolder = Holder.direct(negation);

					Potion normalNegationType = registerPotion(new MobEffectInstance(negationHolder, normalTime), negationBaseName, negationBaseName);
					Potion longNegationType = registerPotion(new MobEffectInstance(negationHolder, longTime), negationBaseName, "long_" + negationBaseName);
					Potion strongNegationType = !hasStrong ? null : registerPotion(new MobEffectInstance(negationHolder, strongTime, 1), negationBaseName, "strong_" + negationBaseName);

					addNegation(flag, normalType, longType, strongType, normalNegationType, longNegationType, strongNegationType);
				}
			}
		}

	}

	public void addPotionMix(String flag, Item reagent, Potion normalType, Potion longType, @Nullable Potion strongType) {
		boolean hasStrong = strongType != null;

		addFlaggedRecipe(flag, Potions.AWKWARD.value(), reagent, normalType);
		addFlaggedRecipe(flag, Potions.WATER.value(), reagent, Potions.MUNDANE.value());

		if (hasStrong)
			addFlaggedRecipe(flag, normalType, Items.GLOWSTONE_DUST, strongType);
		addFlaggedRecipe(flag, normalType, Items.REDSTONE, longType);
	}

	public void addNegation(String flag, Potion normalType, Potion longType, @Nullable Potion strongType,
								   Potion normalNegatedType, Potion longNegatedType, @Nullable Potion strongNegatedType) {
		addFlaggedRecipe(flag, normalType, Items.FERMENTED_SPIDER_EYE, normalNegatedType);

		boolean hasStrong = strongType != null && strongNegatedType != null;

		if (hasStrong) {
			addFlaggedRecipe(flag, strongType, Items.FERMENTED_SPIDER_EYE, strongNegatedType);
			addFlaggedRecipe(flag, normalNegatedType, Items.GLOWSTONE_DUST, strongNegatedType);
		}
		addFlaggedRecipe(flag, longType, Items.FERMENTED_SPIDER_EYE, longNegatedType);
		addFlaggedRecipe(flag, normalNegatedType, Items.REDSTONE, longNegatedType);

	}

	// uglyyy
	protected final Map<Potion, String> modPotionsToConfigFlag = Maps.newHashMap();

	protected void addFlaggedRecipe(String flag, Potion potion, Item reagent, Potion to) {
        modPotionsToConfigFlag.put(to, flag);
		// Supplier<Ingredient> flagIngredientSupplier = () -> new FlagIngredient(reagent, flag, zeta.configManager.getConfigFlagManager(), zeta.configManager.getConfigFlagManager().flagIngredientSerializer);
		if (zeta.configManager.getConfigFlagManager().getFlag(flag)) {
			addBrewingRecipe(potion, reagent, to);
		}
    }

	protected Potion registerPotion(MobEffectInstance eff, String baseName, String name) {
		Potion effect = new Potion(zeta.modid + "." + baseName, eff);
		zeta.registry.register(effect, name, Registries.POTION);
        modPotionsToConfigFlag.put(effect, name);
		return effect;
	}

	public boolean isEnabled(Potion potion) {
		return !modPotionsToConfigFlag.containsKey(potion) || zeta.configManager.getConfigFlagManager().getFlag(modPotionsToConfigFlag.get(potion));
	}

	protected abstract void addBrewingRecipe(Potion input, Item reagentSupplier, Potion output);
}
