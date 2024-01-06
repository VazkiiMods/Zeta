package org.violetmoon.zetaimplforge.mixin.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemColors.class)
public interface AccessorItemColors {

	@Accessor("itemColors")
	Map<Reference<Item>, ItemColor> zeta$getItemColors();

}
