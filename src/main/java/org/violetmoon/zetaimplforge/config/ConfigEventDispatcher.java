package org.violetmoon.zetaimplforge.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.util.zetalist.ZetaList;

public class ConfigEventDispatcher {

	public static void configChanged(ModConfigEvent event) {
		for(Zeta z : ZetaList.INSTANCE.getZetas()) {
			String modid = z.modid;
			if(!event.getConfig().getModId().equals(modid) || z.configInternals == null)
				continue;
				
			// https://github.com/VazkiiMods/Quark/commit/b0e00864f74539d8650cb349e88d0302a0fda8e4
			// "The Forge config api writes to the config file on every single change
			//  to the config, which would cause the file watcher to trigger
			//  a config reload while the config gui is committing changes."
			if(System.currentTimeMillis() - z.configInternals.debounceTime() > 20)
				handleConfigChange(z);
		}
	}

	/* Remove config
	public static void dispatchAllInitialLoads() {
		for(Zeta z : ZetaList.INSTANCE.getZetas())
			handleConfigChange(z);
	}
	 */

	private static void handleConfigChange(Zeta z) {
		z.configManager.onReload();
		z.loadBus.fire(new ZConfigChanged());
	}
}
