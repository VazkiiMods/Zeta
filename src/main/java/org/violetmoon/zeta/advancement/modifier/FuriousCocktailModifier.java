package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;
import java.util.function.BooleanSupplier;

import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.Holder;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class FuriousCocktailModifier extends AdvancementModifier {
	
	private static final ResourceLocation TARGET_AP = ResourceLocation.withDefaultNamespace("nether/all_potions");
	private static final ResourceLocation TARGET_AE = ResourceLocation.withDefaultNamespace("nether/all_effects");

	final BooleanSupplier isPotion;
	final Set<Holder<MobEffect>> effects;
	
	public FuriousCocktailModifier(ZetaModule module, BooleanSupplier isPotion, Set<Holder<MobEffect>> effects) {
		super(module);
		
		this.isPotion = isPotion;
		this.effects = effects;
	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET_AP, TARGET_AE);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		if (!isPotion.getAsBoolean() && res.equals(TARGET_AP)) return false;
		
		Criterion<?> crit = adv.getCriterion("all_effects");
		if (crit != null && crit.triggerInstance() instanceof EffectsChangedTrigger.TriggerInstance ect && ect.effects().isPresent()) {
			for(Holder<MobEffect> e : effects) {
				ect.effects().get().effectMap().put(e, new MobEffectsPredicate.MobEffectInstancePredicate());
			}
			return true;
		}
		return false;
	}
}