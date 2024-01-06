package org.violetmoon.zeta.advancement;

import java.util.Collection;
import java.util.Set;
import java.util.function.BooleanSupplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.advancement.modifier.ASeedyPlaceModifier;
import org.violetmoon.zeta.advancement.modifier.AdventuringTimeModifier;
import org.violetmoon.zeta.advancement.modifier.BalancedDietModifier;
import org.violetmoon.zeta.advancement.modifier.FishyBusinessModifier;
import org.violetmoon.zeta.advancement.modifier.FuriousCocktailModifier;
import org.violetmoon.zeta.advancement.modifier.GlowAndBeholdModifier;
import org.violetmoon.zeta.advancement.modifier.MonsterHunterModifier;
import org.violetmoon.zeta.advancement.modifier.TacticalFishingModifier;
import org.violetmoon.zeta.advancement.modifier.TwoByTwoModifier;
import org.violetmoon.zeta.advancement.modifier.WaxModifier;
import org.violetmoon.zeta.api.IAdvancementModifier;
import org.violetmoon.zeta.api.IAdvancementModifierDelegate;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZAddReloadListener;
import org.violetmoon.zeta.event.load.ZGatherAdvancementModifiers;

public class AdvancementModifierRegistry {
	protected final Zeta zeta;
	protected final Multimap<ResourceLocation, IAdvancementModifier> modifiers = HashMultimap.create();

	protected boolean gatheredAddons = false;

	public AdvancementModifierRegistry(Zeta zeta) {
		this.zeta = zeta;
	}

	public ManualTrigger registerManualTrigger(String resloc) {
		ResourceLocation id = zeta.registry.newResourceLocation(resloc);
		ManualTrigger trigger = new ManualTrigger(id);
		CriteriaTriggers.register(trigger);
		return trigger;
	}

	public void addModifier(IAdvancementModifier mod) {
		for(ResourceLocation r : mod.getTargets())
			modifiers.put(r, mod);
	}

	@LoadEvent
	public void addListeners(ZAddReloadListener event) {
		if(!gatheredAddons) {
			IAdvancementModifierDelegate delegateImpl = new DelegateImpl();
			zeta.loadBus.fireExternal(new ZGatherAdvancementModifiers() {
				@Override
				public void register(IAdvancementModifier modifier) {
					addModifier(modifier);
				}

				@Override
				public IAdvancementModifierDelegate getDelegate() {
					return delegateImpl;
				}
			}, ZGatherAdvancementModifiers.class);

			gatheredAddons = true;
		}

		ServerAdvancementManager advancements = event.getServerResources().getAdvancements();
		event.addListener((ResourceManagerReloadListener) mgr -> onAdvancementsLoaded(advancements));

	}

	private void onAdvancementsLoaded(ServerAdvancementManager manager) {
		for(ResourceLocation res : modifiers.keySet()) {
			Advancement adv = manager.getAdvancement(res);

			if(adv != null) {
				Collection<IAdvancementModifier> found = modifiers.get(res);

				if(!found.isEmpty()) {
					int modifications = 0;
					MutableAdvancement mutable = new MutableAdvancement(adv);

					for(IAdvancementModifier mod : found)
						if(mod.isActive() && mod.apply(res, mutable))
							modifications++;

					if(modifications > 0) {
						zeta.log.info("Modified advancement {} with {} patches", adv.getId(), modifications);
						mutable.commit();
					}
				}
			}
		}
	}

	private static class DelegateImpl implements IAdvancementModifierDelegate {

		@Override
		public IAdvancementModifier createAdventuringTimeMod(Set<ResourceKey<Biome>> locations) {
			return new AdventuringTimeModifier(null, locations);
		}

		@Override
		public IAdvancementModifier createBalancedDietMod(Set<ItemLike> items) {
			return new BalancedDietModifier(null, items);
		}

		@Override
		public IAdvancementModifier createFuriousCocktailMod(BooleanSupplier isPotion, Set<MobEffect> effects) {
			return new FuriousCocktailModifier(null, isPotion, effects);
		}

		@Override
		public IAdvancementModifier createMonsterHunterMod(Set<EntityType<?>> types) {
			return new MonsterHunterModifier(null, types);
		}

		@Override
		public IAdvancementModifier createTwoByTwoMod(Set<EntityType<?>> types) {
			return new TwoByTwoModifier(null, types);
		}

		@Override
		public IAdvancementModifier createWaxOnWaxOffMod(Set<Block> unwaxed, Set<Block> waxed) {
			return new WaxModifier(null, unwaxed, waxed);
		}

		@Override
		public IAdvancementModifier createFishyBusinessMod(Set<ItemLike> fishes) {
			return new FishyBusinessModifier(null,fishes);
		}

		@Override
		public IAdvancementModifier createTacticalFishingMod(Set<BucketItem> buckets) {
			return new TacticalFishingModifier(null, buckets);
		}

		@Override
		public IAdvancementModifier createASeedyPlaceMod(Set<Block> seeds) {
			return new ASeedyPlaceModifier(null,seeds);
		}

		@Override
		public IAdvancementModifier createGlowAndBeholdMod(Set<Block> signs) {
			return new GlowAndBeholdModifier(null, signs);
		}

	}

}
