package org.violetmoon.zeta.api;

import java.util.Set;
import java.util.function.BooleanSupplier;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public interface IAdvancementModifier {

	Set<ResourceLocation> getTargets();
	boolean apply(ResourceLocation res, IMutableAdvancement adv, RegistryAccess registry);


	default IAdvancementModifier setCondition(BooleanSupplier cond){
		return this;
	}

	default boolean isActive(){
		return true;
	}

}
