package org.violetmoon.zetaimplforge.client;

import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
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
import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.client.event.play.ZRenderTooltip;
import org.violetmoon.zeta.client.event.play.ZScreen;
import org.violetmoon.zeta.client.event.play.ZScreenshot;
import org.violetmoon.zeta.util.zetalist.ZetaClientList;
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
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderTick;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZRenderTooltip;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZScreen;
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
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

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
		if(EffectiveSide.get().isServer())
			return ServerLifecycleHooks.getCurrentServer().registryAccess();

		ClientPacketListener conn = Minecraft.getInstance().getConnection();
		return conn == null ? null : conn.registryAccess();
	}

	@Override
	public void start() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		MinecraftForge.EVENT_BUS.addListener(this::clientTick);


		MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlayNeitherPreNorPost);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPre);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPost);
	}

	boolean clientTicked = false;
	public void clientTick(TickEvent.ClientTickEvent e) {
		if(!clientTicked) {
			loadBus.fire(new ZFirstClientTick());
			clientTicked = true;
		}

		playBus.fire(new ForgeZClientTick(e), ZClientTick.class);
	}


	//TODO: This probably should have been a PRE/POST event (just copying quark here)
	public void renderGameOverlayNeitherPreNorPost(RenderGuiOverlayEvent e) {
		if(e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair(e), ZRenderGuiOverlay.Crosshair.class);
		else if(e.getOverlay() == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar(e), ZRenderGuiOverlay.Hotbar.class);
	}

	public void renderGuiOverlayPre(RenderGuiOverlayEvent.Pre e) {
		if (e.getOverlay() == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Pre(e), ZRenderGuiOverlay.Hotbar.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Pre(e), ZRenderGuiOverlay.Crosshair.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Pre(e), ZRenderGuiOverlay.PlayerHealth.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Pre(e), ZRenderGuiOverlay.ArmorLevel.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.DEBUG_TEXT.type())
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Pre(e), ZRenderGuiOverlay.DebugText.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.POTION_ICONS.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Pre(e), ZRenderGuiOverlay.PotionIcons.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Pre(e), ZRenderGuiOverlay.ChatPanel.Pre.class);
	}

	public void renderGuiOverlayPost(RenderGuiOverlayEvent.Post e) {
		NamedGuiOverlay overlay = e.getOverlay();
		if (overlay == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Post(e), ZRenderGuiOverlay.Hotbar.Post.class);
		else if (overlay == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Post(e), ZRenderGuiOverlay.Crosshair.Post.class);
		else if (overlay == VanillaGuiOverlay.PLAYER_HEALTH.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Post(e), ZRenderGuiOverlay.PlayerHealth.Post.class);
		else if (overlay == VanillaGuiOverlay.ARMOR_LEVEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Post(e), ZRenderGuiOverlay.ArmorLevel.Post.class);
		else if (overlay == VanillaGuiOverlay.DEBUG_TEXT.type())
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Post(e), ZRenderGuiOverlay.DebugText.Post.class);
		else if (overlay == VanillaGuiOverlay.POTION_ICONS.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Post(e), ZRenderGuiOverlay.PotionIcons.Post.class);
		else if (overlay == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Post(e), ZRenderGuiOverlay.ChatPanel.Post.class);
	}

}
