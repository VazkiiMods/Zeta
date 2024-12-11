package org.violetmoon.zeta.advancement.modifier;

import java.util.Set;

import net.minecraft.core.RegistryAccess;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;

public class TacticalFishingModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = ResourceLocation.withDefaultNamespace("husbandry/tactical_fishing");
    final Set<BucketItem> bucketItems;

    public TacticalFishingModifier(ZetaModule module, Set<BucketItem> buckets) {
        super(module);
        this.bucketItems = buckets;
        Preconditions.checkArgument(!buckets.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv, RegistryAccess registry) {

        ItemLike[] array = bucketItems.toArray(ItemLike[]::new);
        Criterion<FilledBucketTrigger.TriggerInstance> criterion = FilledBucketTrigger.TriggerInstance.filledBucket(
                ItemPredicate.Builder.item().of(array));

        String name = BuiltInRegistries.ITEM.getKey(array[0].asItem()).toString();
        adv.addOrCriterion(name, criterion);

        return true;
    }

}
