package org.violetmoon.zeta.util;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

// rtx on boys
public abstract class RaytracingUtil {

	public abstract double getEntityRange(LivingEntity player);

	public HitResult rayTrace(Entity entity, Level world, Player player, Block blockMode, Fluid fluidMode) {
		return rayTrace(entity, world, player, blockMode, fluidMode, getEntityRange(player));
	}

	public HitResult rayTrace(Entity entity, Level world, Entity player, Block blockMode, Fluid fluidMode, double range) {
		 Pair<Vec3, Vec3> params = getEntityParams(player);

		return rayTrace(entity, world, params.getLeft(), params.getRight(), blockMode, fluidMode, range);
	}

	public HitResult rayTrace(Entity entity, Level world, Vec3 startPos, Vec3 ray, Block blockMode, Fluid fluidMode, double range) {
		return rayTrace(entity, world, startPos, ray.scale(range), blockMode, fluidMode);
	}

	public HitResult rayTrace(Entity entity, Level world, Vec3 startPos, Vec3 ray, Block blockMode, Fluid fluidMode) {
		Vec3 end = startPos.add(ray);
		ClipContext context = new ClipContext(startPos, end, blockMode, fluidMode, entity);
		return world.clip(context);
	}

	public Pair<Vec3, Vec3> getEntityParams(Entity player) {
		float scale = 1.0F;
		float pitch = player.xRotO + (player.getXRot() - player.xRotO) * scale;
		float yaw = player.yRotO + (player.getYRot() - player.yRotO) * scale;
		Vec3 pos = player.position();
		double posX = player.xo + (pos.x - player.xo) * scale;
		double posY = player.yo + (pos.y - player.yo) * scale;
		if (player instanceof Player)
			posY += player.getEyeHeight();
		double posZ = player.zo + (pos.z - player.zo) * scale;
		Vec3 rayPos = new Vec3(posX, posY, posZ);

		float zYaw = -Mth.cos(yaw * (float) Math.PI / 180);
		float xYaw = Mth.sin(yaw * (float) Math.PI / 180);
		float pitchMod = -Mth.cos(pitch * (float) Math.PI / 180);
		float azimuth = -Mth.sin(pitch * (float) Math.PI / 180);
		float xLen = xYaw * pitchMod;
		float yLen = zYaw * pitchMod;
		Vec3 ray = new Vec3(xLen, azimuth, yLen);

		return Pair.of(rayPos, ray);
	}

}
