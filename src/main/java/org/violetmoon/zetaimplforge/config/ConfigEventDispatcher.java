package org.violetmoon.zetaimplforge.config;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.event.load.ForgeZConfigChange;

public class ConfigEventDispatcher {
	public ConfigEventDispatcher(Zeta z) {
		this.z = z;
	}

	private final Zeta z;

	// The story is:
	// - Forge dispatches ModConfigEvent.Reloading directly from the NightConfig filewatcher thread
	//    (which means it can be received multiple times in a row, as the file save settles)
	//    (and also, updating our data structures/event bus ("refreshing the config") becomes
	//    a thread safety hazard)
	// - Forge seemingly dispatches ModConfigEvent.Reloading for fun sometimes
	//    (including in the middle of other modloading events, and 3-4 times during FMLCommonSetupEvent#enqueueWork)
	// - We call dispatchAllInitialLoads from FMLCommonSetupEvent#enqueueWork
	//    (on the server, this at least reliably gets us onto a thread called 'main'?
	//    who's thread is that? idk)
	// - We don't always have access to a "server thread" because this all can run before the server starts!
	//
	// To try and untangle this mess, here's what we do on the client:
	// - All configs start in the NOT_READY state.
	// - In FMLCommonSetupEvent#enqueueWork we refresh the config once on the enqueueWork thread
	//    and transition to ACCEPT_FILE_RELOADS.
	//
	// here's what we do on the server:
	// - All configs start in the NOT_READY state.
	// - In FMLCommonSetupEvent#enqueueWork we refresh the config once on the enqueueWork thread
	//    and transition to WAITING_FOR_SERVER_START.
	// - In ServerAboutToStartEvent we transition to ACCEPT_FILE_RELOADS.
	//
	// In ModConfigEvent.Reloading, we:
	// - try to find a better thread (which on the client is the render thread, and on the server,
	//    since we wait for the server to start, is hopefully the server thread)
	// - if the config is in ACCEPT_FILE_RELOADS: transition to BUSY, refresh the config, and
	//    transition back to ACCEPT_FILE_RELOADS.
	// - otherwise just drop the request to refresh the config on the floor.
	//    (It's too early, or it's reentrant, and that will cause problems.)
	// This also removes an ugly "debounce-time" hack where we'd just ignore ModConfigEvents
	// if enough time hadn't passed since the last one. You lose the ability to reliably refresh
	// the config *while* the server is starting, but like, that's fine.
	//
	// If this is wrong/incorrect/causing problems, BLAME QUAT
	//
	// Parallel Mod Loading Considered Harmful episode 309128313

	//leaving this comment here but the statemachine seems to subsume the old debounce-time hack
	// https://github.com/VazkiiMods/Quark/commit/b0e00864f74539d8650cb349e88d0302a0fda8e4
	// "The Forge config api writes to the config file on every single change
	//  to the config, which would cause the file watcher to trigger
	//  a config reload while the config gui is committing changes."

	private static final int BEFORE_INIT = 0;
	private static final int WAITING_FOR_SERVER_START = 1;
	private static final int ACCEPT_FILE_RELOADS = 2;
	private static final int BUSY = 3;

	private final AtomicInteger state = new AtomicInteger(BEFORE_INIT);

	public void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			z.log.info("Common setup: Performing initial refresh of {}'s config on thread '{}'", z.modid, Thread.currentThread().getName());

			z.configManager.onReload();
			z.loadBus.fire(new ForgeZConfigChange());

			int oldState;
			if(z.side == ZetaSide.CLIENT) {
				//on the client we always have a render thread, we can start accepting config refreshes now
				oldState = state.getAndSet(ACCEPT_FILE_RELOADS);
				z.log.info("{}'s config is now ready to accept filewatcher changes", z.modid);
			} else {
				//on the server we want to wait for the existence of the server thread to process config refreshes on
				oldState = state.getAndSet(WAITING_FOR_SERVER_START);
				z.log.info("Waiting for server start before accepting filewatcher changes to {}'s config", z.modid);
			}

			if(oldState != BEFORE_INIT)
				z.log.warn("Common setup: {}'s config was previously in state '{}'... weird, but trying to continue. Report this.", z.modid, fmtState(oldState));
		});
	}

	public void serverAboutToStart(ServerAboutToStartEvent e) {
		if(z.side == ZetaSide.SERVER) {
			z.log.info("Server starting, accepting filewatcher changes for {}'s config", z.modid);

			int oldState = state.getAndSet(ACCEPT_FILE_RELOADS);
			if(oldState != WAITING_FOR_SERVER_START)
				z.log.warn("Server start: {}'s config was previously in state '{}'... weird, but trying to continue. Report this.", z.modid, fmtState(oldState));
		}
	}

	public void modConfigReloading(ModConfigEvent.Reloading event) {
		String modid = z.modid;
		if(!event.getConfig().getModId().equals(modid))
			return;

		if(z.configManager == null || z.configInternals == null) {
			z.log.info("Ignoring request to refresh {}'s config WAY too early", z.modid);
			return;
		}

		z.log.info("About to refresh {}'s config, looking for better thread than '{}'...", z.modid, Thread.currentThread().getName());
		z.proxy.tryToExecuteOnMainThread(() -> {
			//if the state is ACCEPT_FILE_RELOADS, transition it to BUSY and continue
			//otherwise drop the request to refresh this config; we might be waiting for
			//FMLCommonSetup/ServerAboutToStart, or it might currently be in the process of refreshing
			int oldState = state.compareAndExchange(ACCEPT_FILE_RELOADS, BUSY);
			if(oldState != ACCEPT_FILE_RELOADS) {
				z.log.info("{}'s config is '{}', ignoring config refresh. Current thread: {}", z.modid, fmtState(oldState), Thread.currentThread().getName());
				return;
			} else
				z.log.info("Refreshing {}'s config on thread '{}'", z.modid, Thread.currentThread().getName());

			try {
				//actually refresh the config
				z.configManager.onReload();
				z.loadBus.fire(new ForgeZConfigChange());
			} finally {
				//ready for another file reload
				z.log.info("All done refreshing {}'s config", z.modid);
				state.setRelease(ACCEPT_FILE_RELOADS);
			}
		});
	}

	private static String fmtState(int state) {
		return switch(state) {
			case BEFORE_INIT -> "BEFORE_INIT";
			case WAITING_FOR_SERVER_START -> "WAITING_FOR_SERVER_START";
			case ACCEPT_FILE_RELOADS -> "ACCEPT_FILE_RELOADS";
			case BUSY -> "BUSY";
			default -> "weird unknown state " + state + "???";
		};
	}
}
