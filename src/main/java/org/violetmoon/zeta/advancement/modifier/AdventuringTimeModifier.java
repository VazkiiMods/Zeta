package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;

import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.collect.ImmutableSet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class AdventuringTimeModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = ResourceLocation.withDefaultNamespace("adventure/adventuring_time");
	
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
			/* TODO: event.getRegistryAccess() IN AdvancementModifierRegistry, pass to here?
			Criterion<?> criterion = PlayerTrigger.TriggerInstance.located(
					LocationPredicate.Builder.inBiome(access.registry(Registries.BIOME).get().getHolderOrThrow(key)));
			 adv.addRequiredCriterion(name, criterion);
			 */
		}
		return true;
	}

}
