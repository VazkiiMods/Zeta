package org.violetmoon.zeta.util.handler;

import static org.violetmoon.zeta.mod.ZetaMod.ZETA;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZLoadComplete;
import org.violetmoon.zeta.event.play.furnace.ZFurnaceFuelBurnTime;
import org.violetmoon.zeta.util.BlockUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class FuelHandler {

	private final Map<Item, Integer> fuelValues = new HashMap<>();
	private final Zeta zeta;
	
	public FuelHandler(Zeta zeta) {
		this.zeta = zeta;
	}
	
	public void addFuel(Item item, int fuel) {
		if(fuel > 0 && item != null && !fuelValues.containsKey(item))
			fuelValues.put(item, fuel);
	}

	public void addFuel(Block block, int fuel) {
		addFuel(block.asItem(), fuel);
	}

	public void addWood(Block block) {
		String regname = Objects.toString(ZETA.registry.getRegistryName(block, BuiltInRegistries.BLOCK));

		if (regname.contains("crimson") || regname.contains("warped"))
			return; //do nothing if block is crimson or warped, since they aren't flammable. #3549
		if (block instanceof ICustomWoodFuelValue fuelBlock)
			addFuel(block, fuelBlock.getBurnTimeInTicksWhenWooden());
		else if (block instanceof SlabBlock)
			addFuel(block, 150);
		else
			addFuel(block, 300);
	}

	@LoadEvent
	public void addAllWoods(ZLoadComplete event) {
		for(Block block : BuiltInRegistries.BLOCK) {
			ResourceLocation regname = zeta.registry.getRegistryName(block, BuiltInRegistries.BLOCK);
			if(block != null && regname.getNamespace().equals(zeta.modid) && BlockUtils.isWoodBased(block.defaultBlockState()))
				addWood(block);
		}
	}

	@PlayEvent
	public void getFuel(ZFurnaceFuelBurnTime event) {
		Item item = event.getItemStack().getItem();
		if(fuelValues.containsKey(item))
			event.setBurnTime(fuelValues.get(item));
	}

	public static interface ICustomWoodFuelValue {
		int getBurnTimeInTicksWhenWooden();
	}
}
