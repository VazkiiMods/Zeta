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
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorBlockColors;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorItemColors;

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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.client.HumanoidArmorModelGetter;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.client.event.load.ZAddItemColorHandlers;
import org.violetmoon.zeta.client.event.load.ZAddModelLayers;
import org.violetmoon.zeta.client.event.load.ZAddModels;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.client.event.load.ZKeyMapping;
import org.violetmoon.zeta.client.event.load.ZModel;
import org.violetmoon.zeta.client.event.load.ZRegisterLayerDefinitions;
import org.violetmoon.zeta.client.event.load.ZRegisterReloadListeners;
import org.violetmoon.zeta.client.event.load.ZTooltipComponents;
import org.violetmoon.zeta.client.event.play.ZClientTick;
import org.violetmoon.zeta.client.event.play.ZFirstClientTick;
import org.violetmoon.zeta.client.event.play.ZGatherTooltipComponents;
import org.violetmoon.zeta.client.event.play.ZHighlightBlock;
import org.violetmoon.zeta.client.event.play.ZInput;
import org.violetmoon.zeta.client.event.play.ZInputUpdate;
import org.violetmoon.zeta.client.event.play.ZRenderContainerScreen;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;
import org.violetmoon.zeta.client.event.play.ZRenderLiving;
import org.violetmoon.zeta.client.event.play.ZRenderPlayer;
import org.violetmoon.zeta.client.event.play.ZRenderFrame;
import org.violetmoon.zeta.client.event.play.ZRenderTooltip;
import org.violetmoon.zeta.client.event.play.ZScreen;
import org.violetmoon.zeta.client.event.play.ZScreenshot;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZAddBlockColorHandlers;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZAddItemColorHandlers;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZAddModelLayers;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZAddModels;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZClientSetup;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZKeyMapping;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZModel;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZRegisterLayerDefinitions;
import org.violetmoon.zetaimplforge.client.event.load.ForgeZTooltipComponents;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZClientTick;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZGatherTooltipComponents;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZHighlightBlock;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZInput;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZInputUpdate;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderContainerScreen;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderGuiOverlay;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderLiving;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderPlayer;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderFrame;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderTooltip;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZScreen;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorBlockColors;
import org.violetmoon.zetaimplforge.mixin.mixins.client.AccessorItemColors;

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
