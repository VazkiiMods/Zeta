package org.violetmoon.zetaimplforge.client;

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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
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
		if(EffectiveSide.get().isServer())
			return ServerLifecycleHooks.getCurrentServer().registryAccess();

		ClientPacketListener conn = Minecraft.getInstance().getConnection();
		return conn == null ? null : conn.registryAccess();
	}

	@Override
	public void start() {
		IEventBus bus = NeoForge.EVENT_BUS;

		bus.addListener(this::registerBlockColors);
		bus.addListener(this::registerItemColors);
		bus.addListener(this::clientSetup);
		bus.addListener(this::registerReloadListeners);
		bus.addListener(this::modelModifyBakingResult);
		bus.addListener(this::modelBakingCompleted);
		bus.addListener(this::modelRegisterAdditional);
		bus.addListener(this::modelRegisterGeometryLoaders);
		bus.addListener(this::modelLayers);
		bus.addListener(this::registerKeybinds);
		bus.addListener(this::registerAdditionalModels);
		bus.addListener(this::registerClientTooltipComponentFactories);
		bus.addListener(this::registerLayerDefinitions);

		NeoForge.EVENT_BUS.addListener(this::renderTick);
		NeoForge.EVENT_BUS.addListener(this::clientTick);
		NeoForge.EVENT_BUS.addListener(this::inputMouseButton);
		NeoForge.EVENT_BUS.addListener(this::inputKey);
		NeoForge.EVENT_BUS.addListener(this::screenshot);
		NeoForge.EVENT_BUS.addListener(this::movementInputUpdate);
		NeoForge.EVENT_BUS.addListener(this::renderBlockHighlight);
		NeoForge.EVENT_BUS.addListener(this::gatherTooltipComponents);

		NeoForge.EVENT_BUS.addListener(this::renderContainerScreenForeground);
		NeoForge.EVENT_BUS.addListener(this::renderContainerScreenBackground);

		NeoForge.EVENT_BUS.addListener(this::renderGameOverlayNeitherPreNorPost);
		NeoForge.EVENT_BUS.addListener(this::renderGuiOverlayPre);
		NeoForge.EVENT_BUS.addListener(this::renderGuiOverlayPost);
		NeoForge.EVENT_BUS.addListener(this::renderPlayerPre);
		NeoForge.EVENT_BUS.addListener(this::renderPlayerPost);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::renderLivingPreHighest);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::renderLivingPostLowest);
		NeoForge.EVENT_BUS.addListener(this::renderTooltipGatherComponents);
		NeoForge.EVENT_BUS.addListener(this::renderTooltipGatherComponentsLow);

		NeoForge.EVENT_BUS.addListener(this::screenInitPre);
		NeoForge.EVENT_BUS.addListener(this::screenInitPost);
		NeoForge.EVENT_BUS.addListener(this::screenRenderPre);
		NeoForge.EVENT_BUS.addListener(this::screenRenderPost);
		NeoForge.EVENT_BUS.addListener(this::screenMouseButtonPressedPre);
		NeoForge.EVENT_BUS.addListener(this::screenMouseButtonPressedPost);
		NeoForge.EVENT_BUS.addListener(this::screenMouseScrolledPre);
		NeoForge.EVENT_BUS.addListener(this::screenMouseScrolledPost);
		NeoForge.EVENT_BUS.addListener(this::screenKeyPressedPre);
		NeoForge.EVENT_BUS.addListener(this::screenKeyPressedPost);
		NeoForge.EVENT_BUS.addListener(this::screenCharacterTypedPre);
		NeoForge.EVENT_BUS.addListener(this::screenCharacterTypedPost);
		NeoForge.EVENT_BUS.addListener(this::screenOpening);
	}

	public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		ZAddBlockColorHandlers e = loadBus.fire(new ForgeZAddBlockColorHandlers(event), ZAddBlockColorHandlers.class);
		loadBus.fire(e.makePostEvent(), ZAddBlockColorHandlers.Post.class);
	}

	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		ZAddItemColorHandlers e = loadBus.fire(new ForgeZAddItemColorHandlers(event), ZAddItemColorHandlers.class);
		loadBus.fire(e.makePostEvent(), ZAddItemColorHandlers.Post.class);
	}

	public void clientSetup(FMLClientSetupEvent event) {
		loadBus.fire(new ForgeZClientSetup(event), ZClientSetup.class);
	}

	public void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		loadBus.fire(new ZRegisterReloadListeners(event::registerReloadListener), ZRegisterReloadListeners.class);
	}

	public void modelModifyBakingResult(ModelEvent.ModifyBakingResult e) {
		loadBus.fire(new ForgeZModel.ModifyBakingResult(e), ZModel.ModifyBakingResult.class);
	}

	public void modelBakingCompleted(ModelEvent.BakingCompleted e) {
		loadBus.fire(new ForgeZModel.BakingCompleted(e), ZModel.BakingCompleted.class);
	}

	public void modelRegisterAdditional(ModelEvent.RegisterAdditional e) {
		loadBus.fire(new ForgeZModel.RegisterAdditional(e), ZModel.RegisterAdditional.class);
	}

	public void modelRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders e) {
		loadBus.fire(new ForgeZModel.RegisterGeometryLoaders(e), ZModel.RegisterGeometryLoaders.class);
	}

	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		loadBus.fire(new ForgeZAddModelLayers(event), ZAddModelLayers.class);
	}

	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		loadBus.fire(new ForgeZKeyMapping(event), ZKeyMapping.class);
	}

	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		loadBus.fire(new ForgeZAddModels(event), ZAddModels.class);
	}

	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		loadBus.fire(new ForgeZTooltipComponents(event), ZTooltipComponents.class);
	}

	public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
		loadBus.fire(new ForgeZRegisterLayerDefinitions(e), ZRegisterLayerDefinitions.class);
	}

	//TODO: move ticker stuff out of forge event handlers, subscribe to them from zeta
	// Also these events are a mess lol; sometimes there's 2 start/end events, sometimes there's
	// one event with multiple Phases... bad
	public void renderTick(RenderFrameEvent e) {
		playBus.fire(new ForgeZRenderFrame(e), ZRenderFrame.class);
	}

	boolean clientTicked = false;
	public void clientTick(ClientTickEvent e) {
		if(!clientTicked) {
			loadBus.fire(new ZFirstClientTick());
			clientTicked = true;
		}

		playBus.fire(new ForgeZClientTick(e), ZClientTick.class);
	}

	public void inputMouseButton(InputEvent.MouseButton e) {
		playBus.fire(new ForgeZInput.MouseButton(e), ZInput.MouseButton.class);
	}

	public void inputKey(InputEvent.Key e) {
		playBus.fire(new ForgeZInput.Key(e), ZInput.Key.class);
	}

	public void screenshot(ScreenshotEvent e) {
		playBus.fire(new ZScreenshot());
	}

	public void movementInputUpdate(MovementInputUpdateEvent e) {
		playBus.fire(new ForgeZInputUpdate(e), ZInputUpdate.class);
	}

	public void renderBlockHighlight(RenderHighlightEvent.Block e) {
		playBus.fire(new ForgeZHighlightBlock(e), ZHighlightBlock.class);
	}

	public void gatherTooltipComponents(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZGatherTooltipComponents(e), ZGatherTooltipComponents.class);
	}

	public void renderContainerScreenForeground(ContainerScreenEvent.Render.Foreground e) {
		playBus.fire(new ForgeZRenderContainerScreen.Foreground(e), ZRenderContainerScreen.Foreground.class);
	}

	public void renderContainerScreenBackground(ContainerScreenEvent.Render.Background e) {
		playBus.fire(new ForgeZRenderContainerScreen.Background(e), ZRenderContainerScreen.Background.class);
	}

	//TODO: This probably should have been a PRE/POST event (just copying quark here)
	public void renderGameOverlayNeitherPreNorPost(RenderGuiLayerEvent e) {
		if(e.getName() == VanillaGuiLayers.CROSSHAIR)
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair(e), ZRenderGuiOverlay.Crosshair.class);
		else if(e.getName() == VanillaGuiLayers.HOTBAR)
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar(e), ZRenderGuiOverlay.Hotbar.class);
	}

	public void renderGuiOverlayPre(RenderGuiLayerEvent.Pre e) {
		if (e.getName() == VanillaGuiLayers.HOTBAR)
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Pre(e), ZRenderGuiOverlay.Hotbar.Pre.class);
		else if (e.getName() == VanillaGuiLayers.CROSSHAIR)
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Pre(e), ZRenderGuiOverlay.Crosshair.Pre.class);
		else if (e.getName() == VanillaGuiLayers.PLAYER_HEALTH)
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Pre(e), ZRenderGuiOverlay.PlayerHealth.Pre.class);
		else if (e.getName() == VanillaGuiLayers.ARMOR_LEVEL)
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Pre(e), ZRenderGuiOverlay.ArmorLevel.Pre.class);
		else if (e.getName() == VanillaGuiLayers.DEBUG_OVERLAY)
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Pre(e), ZRenderGuiOverlay.DebugText.Pre.class);
		else if (e.getName() == VanillaGuiLayers.EFFECTS)
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Pre(e), ZRenderGuiOverlay.PotionIcons.Pre.class);
		else if (e.getName() == VanillaGuiLayers.CHAT)
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Pre(e), ZRenderGuiOverlay.ChatPanel.Pre.class);
	}

	public void renderGuiOverlayPost(RenderGuiLayerEvent.Post e) {
		if (e.getName() == VanillaGuiLayers.HOTBAR)
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Post(e), ZRenderGuiOverlay.Hotbar.Post.class);
		else if (e.getName() == VanillaGuiLayers.CROSSHAIR)
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Post(e), ZRenderGuiOverlay.Crosshair.Post.class);
		else if (e.getName() == VanillaGuiLayers.PLAYER_HEALTH)
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Post(e), ZRenderGuiOverlay.PlayerHealth.Post.class);
		else if (e.getName() == VanillaGuiLayers.ARMOR_LEVEL)
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Post(e), ZRenderGuiOverlay.ArmorLevel.Post.class);
		else if (e.getName() == VanillaGuiLayers.DEBUG_OVERLAY)
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Post(e), ZRenderGuiOverlay.DebugText.Post.class);
		else if (e.getName() == VanillaGuiLayers.EFFECTS)
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Post(e), ZRenderGuiOverlay.PotionIcons.Post.class);
		else if (e.getName() == VanillaGuiLayers.CHAT)
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Post(e), ZRenderGuiOverlay.ChatPanel.Post.class);
	}

	public void renderPlayerPre(RenderPlayerEvent.Pre e) {
		playBus.fire(new ForgeZRenderPlayer.Pre(e), ZRenderPlayer.Pre.class);
	}

	public void renderPlayerPost(RenderPlayerEvent.Post e) {
		playBus.fire(new ForgeZRenderPlayer.Post(e), ZRenderPlayer.Post.class);
	}

	public void renderLivingPreHighest(RenderLivingEvent.Pre<?, ?> e) {
		playBus.fire(new ForgeZRenderLiving.PreHighest(e), ZRenderLiving.PreHighest.class);
	}

	public void renderLivingPostLowest(RenderLivingEvent.Post<?, ?> e) {
		playBus.fire(new ForgeZRenderLiving.PostLowest(e), ZRenderLiving.PostLowest.class);
	}

	public void renderTooltipGatherComponents(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZRenderTooltip.GatherComponents(e), ZRenderTooltip.GatherComponents.class);
	}

	public void renderTooltipGatherComponentsLow(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZRenderTooltip.GatherComponents.Low(e), ZRenderTooltip.GatherComponents.Low.class);
	}

	public void screenInitPre(ScreenEvent.Init.Pre e) {
		playBus.fire(new ForgeZScreen.Init.Pre(e), ZScreen.Init.Pre.class);
	}

	public void screenInitPost(ScreenEvent.Init.Post e) {
		playBus.fire(new ForgeZScreen.Init.Post(e), ZScreen.Init.Post.class);
	}

	public void screenRenderPre(ScreenEvent.Render.Pre e) {
		playBus.fire(new ForgeZScreen.Render.Pre(e), ZScreen.Render.Pre.class);
	}

	public void screenRenderPost(ScreenEvent.Render.Post e) {
		playBus.fire(new ForgeZScreen.Render.Post(e), ZScreen.Render.Post.class);
	}

	public void screenMouseButtonPressedPre(ScreenEvent.MouseButtonPressed.Pre e) {
		playBus.fire(new ForgeZScreen.MouseButtonPressed.Pre(e), ZScreen.MouseButtonPressed.Pre.class);
	}

	public void screenMouseButtonPressedPost(ScreenEvent.MouseButtonPressed.Post e) {
		playBus.fire(new ForgeZScreen.MouseButtonPressed.Post(e), ZScreen.MouseButtonPressed.Post.class);
	}

	public void screenMouseScrolledPre(ScreenEvent.MouseScrolled.Pre e) {
		playBus.fire(new ForgeZScreen.MouseScrolled.Pre(e), ZScreen.MouseScrolled.Pre.class);
	}

	public void screenMouseScrolledPost(ScreenEvent.MouseScrolled.Post e) {
		playBus.fire(new ForgeZScreen.MouseScrolled.Post(e), ZScreen.MouseScrolled.Post.class);
	}

	public void screenKeyPressedPre(ScreenEvent.KeyPressed.Pre e) {
		playBus.fire(new ForgeZScreen.KeyPressed.Pre(e), ZScreen.KeyPressed.Pre.class);
	}

	public void screenKeyPressedPost(ScreenEvent.KeyPressed.Post e) {
		playBus.fire(new ForgeZScreen.KeyPressed.Post(e), ZScreen.KeyPressed.Post.class);
	}

	public void screenCharacterTypedPre(ScreenEvent.CharacterTyped.Pre e) {
		playBus.fire(new ForgeZScreen.CharacterTyped.Pre(e), ZScreen.CharacterTyped.Pre.class);
	}

	public void screenCharacterTypedPost(ScreenEvent.CharacterTyped.Post e) {
		playBus.fire(new ForgeZScreen.CharacterTyped.Post(e), ZScreen.CharacterTyped.Post.class);
	}

	public void screenOpening(ScreenEvent.Opening e) {
		playBus.fire(new ForgeZScreen.Opening(e), ZScreen.Opening.class);
	}
}
