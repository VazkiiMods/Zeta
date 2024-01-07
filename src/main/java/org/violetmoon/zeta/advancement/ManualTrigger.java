package org.violetmoon.zeta.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ManualTrigger extends SimpleCriterionTrigger<ManualTrigger.Instance> {

	final ResourceLocation id;

	public ManualTrigger(ResourceLocation id) {
		this.id = id;
	}

	public void trigger(ServerPlayer player) {
		trigger(player, instance -> true);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	protected @NotNull Instance createInstance(@NotNull JsonObject jsonObject, @NotNull ContextAwarePredicate contextAwarePredicate, @NotNull DeserializationContext deserializationContext) {
		return new Instance(id, contextAwarePredicate);
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation id, ContextAwarePredicate contextAwarePredicate) {
			super(id, contextAwarePredicate);
		}

	}

}
