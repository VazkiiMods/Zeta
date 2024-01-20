package org.violetmoon.zeta.advancement.modifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.mixin.mixins.AccessorContextAwarePredicate;
import org.violetmoon.zeta.mixin.mixins.AccessorItemUsedOnLocationTrigger;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;
import java.util.function.Predicate;

public class WaxModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET_ON = new ResourceLocation("husbandry/wax_on");
    private static final ResourceLocation TARGET_OFF = new ResourceLocation("husbandry/wax_off");

    private final Set<Block> unwaxed;
    private final Set<Block> waxed;

    public WaxModifier(ZetaModule module, Set<Block> unwaxed, Set<Block> waxed) {
        super(module);

        this.unwaxed = unwaxed;
        this.waxed = waxed;

        Preconditions.checkArgument(!unwaxed.isEmpty() || !waxed.isEmpty(), "Advancement modifier list cant be empty");

    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET_ON, TARGET_OFF);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
        String title = res.getPath().replaceAll(".+/", "");
        Criterion criterion = adv.getCriterion(title);
        if (criterion != null && criterion.getTrigger() instanceof ItemUsedOnLocationTrigger.TriggerInstance iib) {
            ContextAwarePredicate predicate = ((AccessorItemUsedOnLocationTrigger.AccessorTriggerInstance) iib).zeta$getLocation();
            Predicate<LootContext> compositePredicates = ((AccessorContextAwarePredicate) predicate).zeta$getCompositePredicates();
            // Yes I know its wordy, yes I know this is stupid. Please forgive me I couldnt make it better.
			Predicate<LootContext> contextPredicate = (res.equals(TARGET_ON)) ? compositePredicates
					.or(((AccessorContextAwarePredicate)
							((AccessorItemUsedOnLocationTrigger.AccessorTriggerInstance)
									ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
											LocationPredicate.Builder.location().setBlock(
													BlockPredicate.Builder.block().of(unwaxed).build()),
											ItemPredicate.Builder.item().of(Items.HONEYCOMB))).zeta$getLocation())
							.zeta$getCompositePredicates()
					) : compositePredicates
					.or(((AccessorContextAwarePredicate)
							((AccessorItemUsedOnLocationTrigger.AccessorTriggerInstance)
									ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
											LocationPredicate.Builder.location().setBlock(
													BlockPredicate.Builder.block().of(waxed).build()),
											ItemPredicate.Builder.item().of(ItemTags.AXES))).zeta$getLocation())
							.zeta$getCompositePredicates());

            ((AccessorContextAwarePredicate) predicate).zeta$setCompositePredicates(contextPredicate);
        }

        return true;
    }

}
