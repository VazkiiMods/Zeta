package org.violetmoon.zeta.config;

import com.google.common.collect.Lists;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.piston.ZetaPistonStructureResolver;

import java.util.List;

public class ZetaGeneralConfig {

	public static final ZetaGeneralConfig INSTANCE = new ZetaGeneralConfig();

	@Config(description = "Disable this to turn off the module system logic that makes features turn off when specified mods with the same content are loaded")
	public static boolean useAntiOverlap = true;

	@Config(
		name = "Use Piston Logic Replacement",
		description = "Enable Zeta's piston structure resolver, needed for some Quark or other mod features. If you're having troubles, try turning this off, but be aware other Zeta-using mods can enable it too."
	)
	public static boolean usePistonLogicRepl = true;

	@Config(description = "Changes the piston push limit. Only has an effect if Zeta's piston structure resolver is in use.")
	@Config.Min(value = 0, exclusive = true)
	public static int pistonPushLimit = 12;
	
	@Config(description = "Set to false to disable the behavior where Zeta will automatically hide any disabled items from creative and JEI")
	public static boolean hideDisabledContent = true;

	@Config(description = "Set to false to disable Zeta's item info when viewing recipe/uses for an item in JEI")
	public static boolean enableJeiItemInfo = true;

	@Config(description = "For JEI info purposes, add any items here to specifically disable their JEI info from Zeta. Note that Zeta already only shows info that's relevant to which features are enabled")
	public static List<String> suppressedInfo = Lists.newArrayList();

	@Config(description = "Set to false to stop Zeta from adding mod items to multi-requirement vanilla advancements")
	public static boolean enableAdvancementModification = true;
	
	@Config(description = "Set to false to stop Zeta mods from adding their own advancements")
	public static boolean enableModdedAdvancements = true;
	
	@Config(description = "Set to true to enable a system that debugs Zeta mod worldgen features. This should ONLY be used if you're asked to by a dev.")
	public static boolean enableWorldgenWatchdog = false;
	
	@Config(description = "Set to true to make the Zeta big worldgen features generate as spheres rather than unique shapes. It's faster, but won't look as cool")
	public static boolean useFastWorldgen = false;

	@Config(description = "Set to true to enable verbose logging of creative tab logic, for debugging purposes")
	public static boolean enableCreativeVerboseLogging = false;

	@Config(description = "Set to true to force all creative tab additions to be added to the end of the creative tabs rather than the middle, as a failsafe")
	public static boolean forceCreativeTabAppends = false;

	private ZetaGeneralConfig() {
		// NO-OP
	}	

	@LoadEvent
	public static void configChanged(ZConfigChanged e) {
		ZetaPistonStructureResolver.GlobalSettings.requestEnabled(Zeta.ZETA_ID, usePistonLogicRepl);
		ZetaPistonStructureResolver.GlobalSettings.requestPushLimit(Zeta.ZETA_ID, pistonPushLimit);
	}
	
}
