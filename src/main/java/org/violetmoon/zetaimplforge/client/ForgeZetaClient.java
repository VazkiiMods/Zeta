package org.violetmoon.zetaimplforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorBlockColors;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorItemColors;

import net.neoforged.neoforge.client.event.*;

public class ForgeZetaClient extends ZetaClient {
    public ForgeZetaClient(Zeta z) {
        super(z);
    }

    @Override
    public @Nullable BlockColor getBlockColor(BlockColors bcs, Block block) {
        return ForgeRegistries.BLOCKS.getDelegate(block)
                .map(ref -> ((AccessorBlockColors) bcs).zeta$getBlockColors().get(ref))
                .orElse(null);
    }

    @Override
    public @Nullable ItemColor getItemColor(ItemColors ics, ItemLike itemlike) {
        return ForgeRegistries.ITEMS.getDelegate(itemlike.asItem())
                .map(ref -> ((AccessorItemColors) ics).zeta$getItemColors().get(ref))
                .orElse(null);
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
        if (EffectiveSide.get().isServer())
            return ServerLifecycleHooks.getCurrentServer().registryAccess();

        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        return conn == null ? null : conn.registryAccess();
    }

}
