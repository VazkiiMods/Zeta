package org.violetmoon.zeta.advancement;

import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

//todo: Check if this works
public class ManualTrigger extends SimpleCriterionTrigger<ManualTrigger.Instance> {

	final ResourceLocation id;

	public ManualTrigger(ResourceLocation id) {
		this.id = id;
	}

	public void trigger(ServerPlayer player) {
		trigger(player, instance -> true);
	}

	@Override
	public Codec<Instance> codec() {
		return null;
	}

	public static class Instance implements SimpleCriterionTrigger.SimpleInstance {

		public Instance(ResourceLocation id, ContextAwarePredicate contextAwarePredicate) {}

		@Override
		public Optional<ContextAwarePredicate> player() {
			return Optional.empty();
		}
	}

}
