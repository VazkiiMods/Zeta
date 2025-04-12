package org.violetmoon.zeta.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import math.fast.SpeedyMath;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class MiscUtil {

	public static final Direction[] HORIZONTALS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};

	public static final DyeColor[] CREATIVE_COLOR_ORDER = new DyeColor[] {
			DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
			DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW,
			DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE,
			DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK
	};

	public static BooleanProperty directionProperty(Direction direction) {
		return switch(direction) {
		case DOWN -> BlockStateProperties.DOWN;
		case UP -> BlockStateProperties.UP;
		case NORTH -> BlockStateProperties.NORTH;
		case SOUTH -> BlockStateProperties.SOUTH;
		case WEST -> BlockStateProperties.WEST;
		case EAST -> BlockStateProperties.EAST;
		};
	}

	/**
	 * Reconstructs the goal selector's list to add in a new goal.
	 *
	 * This is because vanilla doesn't play it safe around CMEs with skeletons.
	 * See: <a href="https://github.com/VazkiiMods/Quark/issues/4356"> Quark Issue 4356.</a>
	 *
	 * If a Skeleton is killed with Thorns damage and drops its weapon, it will reassess its goals.
	 * Because the thorns damage is being dealt during goal execution of the MeleeAttackGoal or RangedBowAttackGoal,
	 * this will cause a CME if the attack goal is not the VERY LAST goal in the set.
	 *
	 * Thankfully, the set GoalSelector uses is Linked, so we can just reconstruct the set and avoid the problem.
	 */
	public static void addGoalJustAfterLatestWithPriority(GoalSelector selector, int priority, Goal goal) {
		Set<WrappedGoal> allGoals = new LinkedHashSet<>(selector.getAvailableGoals());
		WrappedGoal latestWithPriority = null;
		for(WrappedGoal wrappedGoal : allGoals) {
			if(wrappedGoal.getPriority() == priority)
				latestWithPriority = wrappedGoal;
		}

		selector.removeAllGoals(g -> true);
		if(latestWithPriority == null)
			selector.addGoal(priority, goal);

		for(WrappedGoal wrappedGoal : allGoals) {
			selector.addGoal(wrappedGoal.getPriority(), wrappedGoal.getGoal());
			if(wrappedGoal == latestWithPriority)
				selector.addGoal(priority, goal);
		}
	}

	public static void damageStack(ItemStack stack, int dmg, Player player, EquipmentSlot slot) {
		stack.hurtAndBreak(dmg, player, slot);
	}

	public static Vec2 getMinecraftAngles(Vec3 direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = Math.asin(direction.y);
		double yaw = Math.asin(direction.x / Math.cos(pitch));

		return new Vec2((float) (pitch * 180 / Math.PI), (float) (-yaw * 180 / Math.PI));
	}

	/**
	 * possible accuracy errors while using this over {@link MiscUtil#getMinecraftAngles}
	 * <p>
	 * We use SpeedyMath, it's quite a bit faster than java's Math and from the looks has little to no accuracy issues
	 * you should still be careful while using it as accuracy might not be the same as {@link MiscUtil#getMinecraftAngles}
	 * <p>
	 * Below are the JMH results of SpeedyMath and Java's Math
	 * <p>
	 * Benchmark               Mode  Cnt    Score   Error  Units
	 * <p>
	 * MyBenchmark.math        avgt   15  110.567 ± 0.970  ns/op
	 * <p>
	 * MyBenchmark.speedyMath  avgt   15   31.496 ± 0.238  ns/op
	 */
	public static Vec2 getMinecraftAnglesLossy(Vec3 direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = SpeedyMath.asin(direction.y);
		double yaw = SpeedyMath.asin(direction.x / SpeedyMath.cos(pitch));

		return new Vec2((float) (pitch * 180 / Math.PI), (float) (-yaw * 180 / Math.PI));
	}

	public static boolean validSpawnLight(ServerLevelAccessor world, BlockPos pos, RandomSource rand) {
		if(world.getBrightness(LightLayer.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
			return light == 0;
		}
	}

	public static boolean validSpawnLocation(@NotNull EntityType<? extends Mob> type, @NotNull LevelAccessor world, MobSpawnType reason, BlockPos pos) {
		BlockPos below = pos.below();
		if(reason == MobSpawnType.SPAWNER)
			return true;
		BlockState state = world.getBlockState(below);
		return BlockUtils.isStoneBased(state, world, below) && state.isValidSpawn(world, below, type);
	}

	public static void syncTE(BlockEntity tile) {
		Packet<ClientGamePacketListener> packet = tile.getUpdatePacket();

		if(packet != null && tile.getLevel() instanceof ServerLevel serverLevel) {
			serverLevel.getChunkSource().chunkMap
					.getPlayers(new ChunkPos(tile.getBlockPos()), false)
					.forEach(e -> e.connection.send(packet));
		}
	}

	public static ItemStack putIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
		IItemHandler handler = null;

		if (level != null && blockPos != null && level.getBlockState(blockPos).getBlock() instanceof WorldlyContainerHolder holder) {
			handler = new SidedInvWrapper(holder.getContainer(level.getBlockState(blockPos), level, blockPos), face);
		} else if (tile != null && level instanceof Level level1) {
			Optional<IItemHandler> optional = Optional.ofNullable(level1.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, face));
			if (optional.isPresent()) {
				handler = optional.orElse(new ItemStackHandler());
			} else if (tile instanceof WorldlyContainer container) {
				handler = new SidedInvWrapper(container, face);
			} else if (tile instanceof Container container) {
				handler = new InvWrapper(container);
			}
		}

		if (handler != null)
			return (simulate && !doSimulation) ? ItemStack.EMPTY : ItemHandlerHelper.insertItem(handler, stack, simulate);

		return stack;
	}

	public static boolean canPutIntoInv(ItemStack stack, LevelAccessor level, BlockPos blockPos, BlockEntity tile, Direction face, boolean doSimulation) {
		return putIntoInv(stack, level, blockPos, tile, face, true, doSimulation).isEmpty();
	}

	public static BlockState fromString(String key) {
		try {
			BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(key), false);
			BlockState state = result.blockState();
			return state == null ? Blocks.AIR.defaultBlockState() : state;
		} catch (CommandSyntaxException e) {
			return Blocks.AIR.defaultBlockState();
		}
	}

	//gets rid of lambdas that could contain references to blockstate properties we might not have
	public static BlockBehaviour.Properties copyPropertySafe(BlockBehaviour blockBehaviour) {
		BlockBehaviour.Properties p = BlockBehaviour.Properties.ofFullCopy(blockBehaviour);
		p.lightLevel(s -> 0);
		p.offsetType(BlockBehaviour.OffsetType.NONE);
		p.mapColor(blockBehaviour.defaultMapColor());
		return p;
	}

}
