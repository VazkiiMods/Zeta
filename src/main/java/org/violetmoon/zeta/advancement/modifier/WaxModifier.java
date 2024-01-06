package org.violetmoon.zeta.advancement.modifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.violetmoon.zeta.advancement.AdvancementModifier;
import org.violetmoon.zeta.api.IMutableAdvancement;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.Set;

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
		if(criterion != null && criterion.getTrigger() instanceof ItemUsedOnLocationTrigger.TriggerInstance iib) {
			// Yes I know its wordy, yes I know this is stupid. Please forgive me I couldnt make it better.
			iib.location.compositePredicates = (res.equals(TARGET_ON)) ? iib.location.compositePredicates
					.or(ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
							LocationPredicate.Builder.location().setBlock(
									BlockPredicate.Builder.block().of(unwaxed).build()),
							ItemPredicate.Builder.item().of(Items.HONEYCOMB))
							.location.compositePredicates
			) : iib.location.compositePredicates
					.or(ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
							LocationPredicate.Builder.location().setBlock(
									BlockPredicate.Builder.block().of(waxed).build()),
							ItemPredicate.Builder.item().of(ItemTags.AXES))
					.location.compositePredicates);
		}
		
		return true;
	}

}
