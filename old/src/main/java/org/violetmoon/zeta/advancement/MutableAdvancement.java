package org.violetmoon.zeta.advancement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.violetmoon.zeta.api.IMutableAdvancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;

public class MutableAdvancement implements IMutableAdvancement {
	
	final Advancement advancement;
	
	public Map<String, Criterion> criteria;
	public List<List<String>> requirements;
	
	public MutableAdvancement(Advancement advancement) {
		this.advancement = advancement;
		mutabilize();
	}

	@Override
	public void addRequiredCriterion(String name, Criterion criterion) {
		criteria.put(name, criterion);
		requirements.add(Lists.newArrayList(name));
	}

	@Override
	public void addOrCriterion(String name, Criterion criterion) {
		criteria.put(name, criterion);
		requirements.get(0).add(name);	
	}

	@Override
	public Criterion getCriterion(String title) {
		return criteria.get(title);
	}

	private void mutabilize() {
		this.criteria = Maps.newHashMap(advancement.criteria);
		this.requirements = new ArrayList<>();

		String[][] arr = advancement.requirements;
		for(String[] req : arr) {
			List<String> reqList = new ArrayList<>(Arrays.asList(req));
			this.requirements.add(reqList);
		}
	}
	
	public void commit() {
		advancement.criteria = ImmutableMap.copyOf(criteria);
		
		List<String[]> requirementArrays = new ArrayList<>();
		for(List<String> list : requirements) {
			String[] arr = list.toArray(new String[list.size()]);
			requirementArrays.add(arr);
		}

		String[][] arr = requirementArrays.toArray(new String[0][requirementArrays.size()]);
		advancement.requirements = arr;
	}
	
}