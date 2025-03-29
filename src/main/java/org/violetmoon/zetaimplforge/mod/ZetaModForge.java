package org.violetmoon.zetaimplforge.mod;

import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.ModuleFinder;
import org.violetmoon.zeta.network.ZetaModInternalNetwork;
import org.violetmoon.zetaimplforge.ForgeZeta;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(ZetaMod.ZETA_ID)
public class ZetaModForge extends ZetaMod{

	// me no likey this
	// this needs to be created before the mod constructor is as we need it to create the zeta bus.
	public static final ZetaModCommonProxy PROXY = DistExecutor.runForDist(
			() ->
			() ->
					new ZetaModClientProxy(),
			() ->
			() ->
					new ZetaModCommonProxy());

	public ZetaModForge() {
		super(new ForgeZeta(ZetaMod.ZETA_ID, ZetaMod.LOGGER, ZetaGeneralConfig.INSTANCE,
				ModuleFinder.EMPTY, List.of(), ZetaModInternalNetwork.PROTOCOL));

		// creates 2 dist specific objects that will handle zeta specific & loader specific events needed for zeta to work
		PROXY.registerEvents(ZETA);
	}

	
}
