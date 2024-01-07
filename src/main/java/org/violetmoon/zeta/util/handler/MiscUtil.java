package org.violetmoon.zeta.util.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.BlockUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MiscUtil {

	public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(Zeta.ZETA_ID, "textures/gui/general_icons.png");

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

	public static void damageStack(Player player, InteractionHand hand, ItemStack stack, int dmg) {
		stack.hurtAndBreak(dmg, player, (p) -> p.broadcastBreakEvent(hand));
	}

	public static Vec2 getMinecraftAngles(Vec3 direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = Math.asin(direction.y);
		double yaw = Math.asin(direction.x / Math.cos(pitch));

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

		if(level != null && blockPos != null && level.getBlockState(blockPos).getBlock() instanceof WorldlyContainerHolder holder) {
			handler = new SidedInvWrapper(holder.getContainer(level.getBlockState(blockPos), level, blockPos), face);
		} else if(tile != null) {
			LazyOptional<IItemHandler> opt = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, face);
			if(opt.isPresent())
				handler = opt.orElse(new ItemStackHandler());
			else if(tile instanceof WorldlyContainer container)
				handler = new SidedInvWrapper(container, face);
			else if(tile instanceof Container container)
				handler = new InvWrapper(container);
		}

		if(handler != null)
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
		BlockBehaviour.Properties p = BlockBehaviour.Properties.copy(blockBehaviour);
		p.lightLevel(s -> 0);
		p.offsetType(BlockBehaviour.OffsetType.NONE);
		p.mapColor(blockBehaviour.defaultMapColor());
		return p;
	}

	public static class Client {
		private static int progress;

		private static final int BASIC_GUI_TEXT_COLOR = 0x404040;

		public static int getGuiTextColor(String name) {
			return getGuiTextColor(name, BASIC_GUI_TEXT_COLOR);
		}

		public static int getGuiTextColor(String name, int base) {
			int ret = base;

			String hex = I18n.get("zeta.gui.color." + name);
			if(hex.matches("#[A-F0-9]{6}"))
				ret = Integer.valueOf(hex.substring(1), 16);
			return ret;
		}

		public static void drawChatBubble(GuiGraphics guiGraphics, int x, int y, Font font, String text, float alpha, boolean extendRight) {
			PoseStack matrix = guiGraphics.pose();

			matrix.pushPose();
			matrix.translate(0, 0, 200);
			int w = font.width(text);
			int left = x - (extendRight ? 0 : w);
			int top = y - 8;

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

			if(extendRight) {
				guiGraphics.blit(MiscUtil.GENERAL_ICONS, left, top, 227, 9, 6, 17, 256, 256);
				for(int i = 0; i < w; i++)
					guiGraphics.blit(MiscUtil.GENERAL_ICONS, left + i + 6, top, 232, 9, 1, 17, 256, 256);
				guiGraphics.blit(MiscUtil.GENERAL_ICONS, left + w + 5, top, 236, 9, 5, 17, 256, 256);
			} else {
				guiGraphics.blit(MiscUtil.GENERAL_ICONS, left, top, 242, 9, 5, 17, 256, 256);
				for(int i = 0; i < w; i++)
					guiGraphics.blit(MiscUtil.GENERAL_ICONS, left + i + 5, top, 248, 9, 1, 17, 256, 256);
				guiGraphics.blit(MiscUtil.GENERAL_ICONS, left + w + 5, top, 250, 9, 6, 17, 256, 256);
			}

			int alphaInt = (int) (256F * alpha) << 24;
			guiGraphics.drawString(font, text, left + 5, top + 3, alphaInt, false);
			matrix.popPose();
		}
	}
}
