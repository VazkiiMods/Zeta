package org.violetmoon.zeta.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import org.violetmoon.zeta.api.IMutableAdvancement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MutableAdvancement implements IMutableAdvancement {
	
	final Advancement advancement;
	
	public Map<String, Criterion<?>> criteria;
	public List<List<String>> requirements;
	
	public MutableAdvancement(Advancement advancement) {
		this.advancement = advancement;
		mutabilize();
	}

	@Override
	public void addRequiredCriterion(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		requirements.add(Lists.newArrayList(name));
	}

	@Override
	public void addOrCriterion(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		requirements.getFirst().add(name);
	}

	@Override
	public Criterion<?> getCriterion(String title) {
		return criteria.get(title);
	}

	private void mutabilize() {
		this.criteria = Maps.newHashMap(advancement.criteria());
		this.requirements = new ArrayList<>();
		AdvancementRequirements advReq = advancement.requirements();
        this.requirements.addAll(advReq.requirements());
	}
	
	public void commit() {
		// advancement.criteria = ImmutableMap.copyOf(criteria); Replace entirely?
		advancement.criteria().clear();
		advancement.criteria().putAll(ImmutableMap.copyOf(criteria));
		
		List<String[]> requirementArrays = new ArrayList<>();
		for(List<String> list : requirements) {
			String[] arr = list.toArray(new String[list.size()]);
			requirementArrays.add(arr);
		}

		// Can't replace this yet
		String[][] arr = requirementArrays.toArray(new String[0][requirementArrays.size()]);
		advancement.requirements = arr;
	}
}