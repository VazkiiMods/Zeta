package org.violetmoon.zeta.advancement.modifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

public class BalancedDietModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = new ResourceLocation("husbandry/balanced_diet");

    private final Set<ItemLike> items;

    public BalancedDietModifier(ZetaModule module, Set<ItemLike> items) {
        super(module);
        this.items = items;
        Preconditions.checkArgument(!items.isEmpty(), "Advancement modifier list cant be empty");

    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
        ItemLike[] array = items.toArray(ItemLike[]::new);

        Criterion criterion = new Criterion(ConsumeItemTrigger.TriggerInstance.usedItem(
                ItemPredicate.Builder.item().of(array).build()));

        String name = BuiltInRegistries.ITEM.getKey(array[0].asItem()).toString();

        adv.addRequiredCriterion(name, criterion);

        return true;
    }

}
