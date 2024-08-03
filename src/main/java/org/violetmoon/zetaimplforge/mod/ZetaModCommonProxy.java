package org.violetmoon.zetaimplforge.mod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.client.event.load.*;
import org.violetmoon.zeta.client.event.play.*;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.event.load.*;
import org.violetmoon.zeta.event.play.*;
import org.violetmoon.zeta.event.play.entity.*;
import org.violetmoon.zeta.event.play.entity.living.*;
import org.violetmoon.zeta.event.play.entity.player.*;
import org.violetmoon.zeta.event.play.loading.*;
import org.violetmoon.zeta.util.handler.RecipeCrawlHandler;
import org.violetmoon.zeta.util.handler.ToolInteractionHandler;
import org.violetmoon.zeta.world.EntitySpawnHandler;
import org.violetmoon.zeta.world.WorldGenHandler;
import org.violetmoon.zetaimplforge.api.ForgeZGatherAdvancementModifiers;
import org.violetmoon.zetaimplforge.capability.ForgeCapabilityManager;
import org.violetmoon.zetaimplforge.client.event.load.*;
import org.violetmoon.zetaimplforge.client.event.play.*;
import org.violetmoon.zetaimplforge.config.ConfigEventDispatcher;
import org.violetmoon.zetaimplforge.event.ForgeEventsRemapper;
import org.violetmoon.zetaimplforge.event.load.*;
import org.violetmoon.zetaimplforge.event.play.*;
import org.violetmoon.zetaimplforge.event.play.entity.*;
import org.violetmoon.zetaimplforge.event.play.entity.living.*;
import org.violetmoon.zetaimplforge.event.play.entity.player.*;
import org.violetmoon.zetaimplforge.event.play.loading.*;
import org.violetmoon.zetaimplforge.world.ZetaBiomeModifier;

import java.util.function.Function;

public class ZetaModCommonProxy {

	public ZetaModCommonProxy(Zeta zeta) {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);

		zeta.loadBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(EntitySpawnHandler.class)
				.subscribe(WorldGenHandler.class)
				.subscribe(ZetaGeneralConfig.class);

		zeta.playBus
				.subscribe(RecipeCrawlHandler.class)
				.subscribe(ToolInteractionHandler.class)
				.subscribe(SyncedFlagHandler.class);


		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);
		ZetaBiomeModifier.registerBiomeModifier(FMLJavaModLoadingContext.get().getModEventBus());

	}


	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(ConfigEventDispatcher::dispatchAllInitialLoads);
	}


}
