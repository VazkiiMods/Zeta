package org.violetmoon.zeta.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class VanillaWoods {
	public record Wood(String name, Block log, Block wood, Block planks, Block leaf, Block fence, boolean nether, SoundType soundWood, SoundType soundPlanks) {}

	public static Wood OAK = new Wood("oak", Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.OAK_PLANKS, Blocks.OAK_LEAVES, Blocks.OAK_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood SPRUCE = new Wood("spruce", Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LEAVES, Blocks.SPRUCE_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood BIRCH = new Wood("birch", Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.BIRCH_PLANKS, Blocks.BIRCH_LEAVES, Blocks.BIRCH_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood JUNGLE = new Wood("jungle", Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LEAVES, Blocks.JUNGLE_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood ACACIA = new Wood("acacia", Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.ACACIA_PLANKS, Blocks.ACACIA_LEAVES, Blocks.ACACIA_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood DARK_OAK = new Wood("dark_oak", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LEAVES, Blocks.DARK_OAK_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood MANGROVE = new Wood("mangrove", Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LEAVES, Blocks.MANGROVE_FENCE, false, SoundType.WOOD, SoundType.WOOD);
	public static Wood BAMBOO = new Wood("bamboo", Blocks.BAMBOO_BLOCK, null, Blocks.BAMBOO_PLANKS, null, Blocks.BAMBOO_FENCE, false, SoundType.BAMBOO_WOOD, SoundType.BAMBOO_WOOD);
	public static Wood CHERRY = new Wood("cherry", Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.CHERRY_PLANKS, Blocks.CHERRY_LEAVES, Blocks.CHERRY_FENCE, false, SoundType.CHERRY_WOOD, SoundType.CHERRY_WOOD);

	public static Wood CRIMSON = new Wood("crimson", Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS, null, Blocks.CRIMSON_FENCE, true, SoundType.STEM, SoundType.NETHER_WOOD);
	public static Wood WARPED = new Wood("warped", Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.WARPED_PLANKS, null, Blocks.WARPED_FENCE, true, SoundType.STEM, SoundType.NETHER_WOOD);

	public static final Wood[] OVERWORLD_NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, MANGROVE, BAMBOO, CHERRY
	};

	public static final Wood[] OVERWORLD = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, MANGROVE, BAMBOO, CHERRY
	};

	public static final Wood[] OVERWORLD_WITH_TREE = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, MANGROVE, CHERRY
	};

	public static final Wood[] NETHER = new Wood[] {
			CRIMSON, WARPED
	};

	public static final Wood[] ALL = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED, MANGROVE, BAMBOO, CHERRY
	};

	public static final Wood[] ALL_WITH_LOGS = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED, MANGROVE, CHERRY
	};

	public static final Wood[] NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED, MANGROVE, BAMBOO, CHERRY
	};
}
