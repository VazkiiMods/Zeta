package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;

import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ASeedyPlaceModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = ResourceLocation.withDefaultNamespace("husbandry/plant_seed");

	final Set<Block> seeds;

	public ASeedyPlaceModifier(ZetaModule module, Set<Block> seeds) {
		super(module);
		this.seeds = seeds;

	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		for(var block : seeds) {
			Criterion criterion = EnterBlockTrigger.TriggerInstance.entersBlock(block);
			
			String name = BuiltInRegistries.BLOCK.getKey(block).toString();
			adv.addOrCriterion(name, criterion);
		}
		
		return true;
	}

}
