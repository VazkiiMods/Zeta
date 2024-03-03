package org.violetmoon.zeta.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

public class ForgeZetaClient extends ZetaClient {
	public ForgeZetaClient(Zeta z) {
		super(z);
	}

	@Override
	public @Nullable BlockColor getBlockColor(BlockColors bcs, Block block) {
		return BuiltInRegistries.BLOCK.stream()
			.map(ref -> ((AccessorBlockColors) bcs).zeta$getBlockColors().get(ref))
			.orElse(null);
	}

	@Override
	public @Nullable ItemColor getItemColor(ItemColors ics, ItemLike itemlike) {
		return BuiltInRegistries.ITEM.stream()
			.map(ref -> ((AccessorItemColors) ics).zeta$getItemColors().get(ref))
			.orElse(null);
	}

	@Override
	public ClientRegistryExtension createClientRegistryExtension() {
		return new FabricClientRegistryExtension(zeta);
	}

	@Override
	public void setBlockEntityWithoutLevelRenderer(Item item, BlockEntityWithoutLevelRenderer bewlr) {
		((IZetaForgeItemStuff) item).zeta$setBlockEntityWithoutLevelRenderer(bewlr);
	}

	@Override
	public void setHumanoidArmorModel(Item item, HumanoidArmorModelGetter modelGetter) {
		((IZetaForgeItemStuff) item).zeta$setHumanoidArmorModel(modelGetter);
	}

	@Override
	public RegistryAccess hackilyGetCurrentClientLevelRegistryAccess() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return Minecraft.getInstance().getSingleplayerServer().registryAccess(); //Todo: I may not actually understand what this means

		ClientPacketListener conn = Minecraft.getInstance().getConnection();
		return conn == null ? null : conn.registryAccess();
	}

	@Override
	public void start() {}
}
