package org.violetmoon.zetaimplforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorBlockColors;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorItemColors;

public class ForgeZetaClient extends ZetaClient {
    public ForgeZetaClient(Zeta z) {
        super(z);
    }

    @Override
    public @Nullable BlockColor getBlockColor(BlockColors bcs, Block block) {
        return ((AccessorBlockColors) bcs).zeta$getBlockColors().get(block);
    }

    @Override
    public @Nullable ItemColor getItemColor(ItemColors ics, ItemLike itemlike) {
        return ((AccessorItemColors) ics).zeta$getItemColors().get(itemlike);
    }

    @Override
    public ClientRegistryExtension createClientRegistryExtension() {
        return new ForgeClientRegistryExtension(zeta);
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
		if (ServerLifecycleHooks.getCurrentServer() == null) {
			if (FMLLoader.getDist().isClient() && Minecraft.getInstance().level != null) {
				return Minecraft.getInstance().level.registryAccess();
			}
		} else {
			return ServerLifecycleHooks.getCurrentServer().registryAccess();
		}
		return null;
    }

}
