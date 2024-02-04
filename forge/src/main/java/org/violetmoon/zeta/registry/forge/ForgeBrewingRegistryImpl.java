package org.violetmoon.zeta.registry.forge;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.violetmoon.zeta.mixin.mixins.AccessorPotionBrewing;

import java.util.function.Supplier;

public class ForgeBrewingRegistryImpl {
    public static void setupRegister(Potion input, Supplier<Ingredient> reagentSupplier, Potion output) {
        AccessorPotionBrewing.zeta$getPotionMixes().add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, input, reagentSupplier.get(), output));
    }
}
