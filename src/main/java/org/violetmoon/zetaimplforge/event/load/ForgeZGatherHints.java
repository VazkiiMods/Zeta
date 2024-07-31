package org.violetmoon.zetaimplforge.event.load;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.event.play.loading.ZGatherHints;
import org.violetmoon.zeta.module.ZetaModule;

public class ForgeZGatherHints extends Event implements ZGatherHints {
	private final ZGatherHints wrapped;

	public ForgeZGatherHints(ZGatherHints e) {
		this.wrapped = e;
	}

	@Override
	public void accept(ItemLike itemLike, Component extra) {
		wrapped.accept(itemLike, extra);
	}

	@Override
	public RegistryAccess getRegistryAccess() {
		return wrapped.getRegistryAccess();
	}

	@Override
	public void hintItem(Zeta zeta, ItemLike itemLike, Object... extra) {
		wrapped.hintItem(zeta, itemLike, extra);
	}

	@Override
	public void hintItem(Zeta zeta, ItemLike itemLike, String key, Object... extra) {
		wrapped.hintItem(zeta, itemLike, key, extra);
	}

	@Override
	public void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm) {
		wrapped.gatherHintsFromModule(module, cfm);
	}
}
