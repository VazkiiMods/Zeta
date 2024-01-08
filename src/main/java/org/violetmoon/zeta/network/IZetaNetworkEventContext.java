package org.violetmoon.zeta.network;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;

public interface IZetaNetworkEventContext {
	CompletableFuture<Void> enqueueWork(Runnable runnable);
	@Nullable ServerPlayer getSender();
	void reply(ZetaHandshakeMessage msg);
}
