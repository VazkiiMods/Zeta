package org.violetmoon.zeta.advancement.modifier;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

public class MonsterHunterModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET_ONE = new ResourceLocation("adventure/kill_a_mob");
	private static final ResourceLocation TARGET_ALL = new ResourceLocation("adventure/kill_all_mobs");
	
	final Set<EntityType<?>> entityTypes;
	
	public MonsterHunterModifier(ZetaModule module, Set<EntityType<?>> entityTypes) {
		super(module);
		
		this.entityTypes = entityTypes;
	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET_ONE, TARGET_ALL);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		boolean all = res.equals(TARGET_ALL);
		
		for(EntityType<?> type : entityTypes) {
			Criterion criterion = new Criterion(KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))));
			
			String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
			if(all)
				adv.addRequiredCriterion(name, criterion);
			else adv.addOrCriterion(name, criterion);
		}
		
		return true;
	}

}
