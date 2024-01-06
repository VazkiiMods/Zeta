package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;

import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class AdventuringTimeModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = new ResourceLocation("adventure/adventuring_time");
	
	private final Set<ResourceKey<Biome>> locations;
	
	public AdventuringTimeModifier(ZetaModule module, Set<ResourceKey<Biome>> locations) {
		super(module);
		this.locations = locations;
	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		for(ResourceKey<Biome> key : locations) {
			String name = key.location().toString();
			
			Criterion criterion = new Criterion(PlayerTrigger.TriggerInstance.located(
					LocationPredicate.inBiome(key)));
			adv.addRequiredCriterion(name, criterion);
		}
		
		return true;
	}

}
