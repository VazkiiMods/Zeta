package org.violetmoon.zeta;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.IFabricBlockBlockExtensions;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.capability.ForgeCapabilityManager;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.config.ForgeBackedConfig;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.TerribleForgeConfigHackery;
import org.violetmoon.zeta.item.IFabricItemItemExtensions;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.network.FabricZetaNetworkHandler;
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.FabricRaytracingUtil;
import org.violetmoon.zeta.util.RaytracingUtil;
import org.violetmoon.zeta.util.ZetaSide;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class FabricZeta extends Zeta {
	public FabricZeta(String modid, Logger log) {
		super(modid, log, ZetaSide.fromClient(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT));
	}

	@Override
	public boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	@Override
	public @Nullable String getModDisplayName(String modid) {
		return FabricLoader.getInstance().getModContainer(modid).orElseThrow().getMetadata().getName();
	}

	@Override
	public IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection) {
		ForgeConfigSpec.Builder bob = new ForgeConfigSpec.Builder();
		ForgeBackedConfig forge = new ForgeBackedConfig(rootSection, bob);
		ForgeConfigSpec spec = bob.build();

		TerribleForgeConfigHackery.registerAndLoadConfigEarlierThanUsual(spec);

		return forge;
	}

	@Override
	public ZetaRegistry createRegistry() {
		return new FabricZetaRegistry(this);
	}

	@Override
	public CraftingExtensionsRegistry createCraftingExtensionsRegistry() {
		return new FabricCraftingExtensionsRegistry();
	}

	@Override
	public BrewingRegistry createBrewingRegistry() {
		return new BrewingRegistry(this);
	}

	@Override
	public PottedPlantRegistry createPottedPlantRegistry() {
		return (resloc, potted) -> ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(resloc, () -> potted);
	}

	@Override
	public ZetaCapabilityManager createCapabilityManager() {
		return new ForgeCapabilityManager();
	}

	@Override
	public BlockExtensionFactory createBlockExtensionFactory() {
		return block -> IFabricBlockBlockExtensions.INSTANCE;
	}

	@Override
	public ItemExtensionFactory createItemExtensionFactory() {
		return stack -> IFabricItemItemExtensions.INSTANCE;
	}

	@Override
	public RaytracingUtil createRaytracingUtil() {
		return new FabricRaytracingUtil();
	}

	@Override
	public ZetaNetworkHandler createNetworkHandler(int protocolVersion) {
		return new FabricZetaNetworkHandler(this, protocolVersion);
	}

	@Override
	public <E, T extends E> T fireExternalEvent(T impl) {
		return null;
	}

	@Override
	public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
		return false;
	}

	@Override
	public void start() {

	}
}
