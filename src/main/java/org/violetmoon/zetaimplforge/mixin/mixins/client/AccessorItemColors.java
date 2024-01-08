package org.violetmoon.zetaimplforge.mixin.mixins.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.item.Item;

@Mixin(ItemColors.class)
public interface AccessorItemColors {

	@Accessor("itemColors")
	Map<Reference<Item>, ItemColor> zeta$getItemColors();

}
