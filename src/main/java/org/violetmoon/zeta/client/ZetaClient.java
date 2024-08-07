package org.violetmoon.zeta.client;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.config.ClientConfigManager;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.bus.ZetaEventBus;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.util.zetalist.IZeta;
import org.violetmoon.zeta.util.zetalist.ZetaClientList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public abstract class ZetaClient implements IZeta {

	public ZetaClient(Zeta zeta) {
		this.zeta = zeta;
		this.loadBus = zeta.loadBus;
		this.playBus = zeta.playBus;

		this.clientConfigManager = createClientConfigManager();
		this.clientRegistryExtension = createClientRegistryExtension();

		loadBus.subscribe(clientRegistryExtension)
			.subscribe(clientConfigManager);

		ZetaClientList.INSTANCE.register(this);
	}

	public final Zeta zeta;
	public final ZetaEventBus<IZetaLoadEvent> loadBus;
	public final ZetaEventBus<IZetaPlayEvent> playBus;

	public ResourceLocation generalIcons = new ResourceLocation("zeta", "textures/gui/general_icons.png");

	public final ClientConfigManager clientConfigManager;
	public final ClientRegistryExtension clientRegistryExtension;

	public ClientConfigManager createClientConfigManager() {
		return new ClientConfigManager(this);
	}

	//ummm ??
	public void sendToServer(IZetaMessage msg) {
		if(Minecraft.getInstance().getConnection() == null)
			return;

		zeta.network.sendToServer(msg);
	}

	//kinda a grab bag of stuff that needs to happen client-only; hmm, not the best design
	public abstract ClientRegistryExtension createClientRegistryExtension();

	//forge makes these weird
	public abstract @Nullable BlockColor getBlockColor(BlockColors bcs, Block block);
	public abstract @Nullable ItemColor getItemColor(ItemColors ics, ItemLike itemlike);

	public abstract void setBlockEntityWithoutLevelRenderer(Item item, BlockEntityWithoutLevelRenderer bewlr);
	public abstract void setHumanoidArmorModel(Item item, HumanoidArmorModelGetter modelGetter);

	//TODO: CAREFULLY evaluate usages of this function, do not use it willy nilly. Sometimes it is necessary though.
	// The name is unwieldy on purpose, usages of this function should stick out.
	public abstract @Nullable RegistryAccess hackilyGetCurrentClientLevelRegistryAccess();

	@Override
	public Zeta asZeta() {
		return zeta;
	}
}
