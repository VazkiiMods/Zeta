package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

public class TwoByTwoModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = new ResourceLocation("husbandry/bred_all_animals");
	
	final Set<EntityType<?>> entityTypes;
	
	public TwoByTwoModifier(ZetaModule module, Set<EntityType<?>> entityTypes) {
		super(module);
		
		this.entityTypes = entityTypes;
		Preconditions.checkArgument(!entityTypes.isEmpty(), "Advancement modifier list cant be empty");

	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		for(EntityType<?> type : entityTypes) {
			Criterion criterion = new Criterion(BredAnimalsTrigger.TriggerInstance
					.bredAnimals(EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))));
			
			String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
			adv.addRequiredCriterion(name, criterion);
		}
		
		return true;
	}

}
