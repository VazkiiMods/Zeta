package org.violetmoon.zeta.advancement;

import com.mojang.serialization.Codec;

import com.mojang.serialization.codecs.RecordCodecBuilder;
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
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ManualTrigger.Instance> CODEC = RecordCodecBuilder.create(
            instanceInstance -> instanceInstance.group(
                    ContextAwarePredicate.CODEC.optionalFieldOf("predicate").forGetter(Instance::player)
            ).apply(instanceInstance, Instance::new)
        );
	}
}
