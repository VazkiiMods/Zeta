package org.violetmoon.zeta.event.load;

import java.util.Set;
import java.util.function.BooleanSupplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.api.IAdvancementModifier;
import org.violetmoon.zeta.api.IAdvancementModifierDelegate;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZGatherAdvancementModifiers extends IZetaLoadEvent {
	void register(IAdvancementModifier modifier);

	IAdvancementModifierDelegate getDelegate();

	default IAdvancementModifier createAdventuringTimeMod(Set<ResourceKey<Biome>> locations) {
		return getDelegate().createAdventuringTimeMod(locations);
	}
	default IAdvancementModifier createBalancedDietMod(Set<ItemLike> items) {
		return getDelegate().createBalancedDietMod(items);
	}
	default IAdvancementModifier createFuriousCocktailMod(BooleanSupplier isPotion, Set<MobEffect> effects) {
		return getDelegate().createFuriousCocktailMod(isPotion, effects);
	}
	default IAdvancementModifier createMonsterHunterMod(Set<EntityType<?>> types) {
		return getDelegate().createMonsterHunterMod(types);
	}
	default IAdvancementModifier createTwoByTwoMod(Set<EntityType<?>> types) {
		return getDelegate().createTwoByTwoMod(types);
	}
	default IAdvancementModifier createWaxOnWaxOffMod(Set<Block> unwaxed, Set<Block> waxed) {
		return getDelegate().createWaxOnWaxOffMod(unwaxed, waxed);
	}
	default IAdvancementModifier createFishyBusinessMod(Set<ItemLike> fishes) {
		return getDelegate().createFishyBusinessMod(fishes);
	}
	default IAdvancementModifier createTacticalFishingMod(Set<BucketItem> buckets) {
		return getDelegate().createTacticalFishingMod(buckets);
	}
	default IAdvancementModifier createASeedyPlaceMod(Set<Block> seeds) {
		return getDelegate().createASeedyPlaceMod(seeds);
	}
	default IAdvancementModifier createGlowAndBeholdMod(Set<Block> seeds) {
		return getDelegate().createGlowAndBeholdMod(seeds);
	}
}
