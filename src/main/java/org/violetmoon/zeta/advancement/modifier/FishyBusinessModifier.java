package org.violetmoon.zeta.advancement.modifier;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class FishyBusinessModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = ResourceLocation.withDefaultNamespace("husbandry/fishy_business");

    final Set<ItemLike> fishes;

    public FishyBusinessModifier(ZetaModule module, Set<ItemLike> fishes) {
        super(module);
        this.fishes = fishes;
        Preconditions.checkArgument(!fishes.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv, RegistryAccess registry) {
        ItemLike[] array = fishes.toArray(ItemLike[]::new);
        Criterion<FishingRodHookedTrigger.TriggerInstance> criterion = FishingRodHookedTrigger.TriggerInstance.fishedItem(
                Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(array).build()));
        String name = BuiltInRegistries.ITEM.getKey(array[0].asItem()).toString();
        adv.addOrCriterion(name, criterion);
        return true;
    }
}