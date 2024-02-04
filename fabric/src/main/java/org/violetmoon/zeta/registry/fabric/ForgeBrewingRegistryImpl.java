package org.violetmoon.zeta.registry.fabric;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import org.violetmoon.zeta.mixin.mixins.AccessorPotionBrewing;

import java.util.function.Supplier;

public class ForgeBrewingRegistryImpl {
    public static void setupRegister(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
        AccessorPotionBrewing.zeta$getPotionMixes().add(new PotionBrewing.Mix<>(input, reagentSupplier.get(), output));
    }
}
