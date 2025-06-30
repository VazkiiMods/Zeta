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
	
	Advancement advancement;
	
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
	public void removeCriterion(String name) {
		criteria.remove(name);
	}

	@Override
	public void replaceCriterion(String name, Criterion<?> criterion) {
		criteria.replace(name, criterion);
	}

	@Override
	public Criterion<?> getCriterion(String title) {
		return criteria.get(title);
	}

	private void mutabilize() {
		this.criteria = Maps.newHashMap(advancement.criteria());
		this.requirements = new ArrayList<>();
		AdvancementRequirements advReq = advancement.requirements();
		for (List<String> requirement : advReq.requirements()) {
			List<String> replcRequirement = new ArrayList<>();
			replcRequirement.addAll(requirement);
			this.requirements.add(replcRequirement);
		}
        //this.requirements.addAll(advReq.requirements());
	}
	
	public void commit() {
		advancement = new Advancement(advancement.parent(), advancement.display(), advancement.rewards(), ImmutableMap.copyOf(criteria), new AdvancementRequirements(requirements), advancement.sendsTelemetryEvent(), advancement.name());
		//advancement.criteria().clear();
		//advancement.criteria().putAll(ImmutableMap.copyOf(criteria));
		//advancement.requirements().requirements().addAll(requirements);
	}
}