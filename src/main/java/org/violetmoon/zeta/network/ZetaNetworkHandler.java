package org.violetmoon.zeta.network;

import java.util.List;
import java.util.function.Function;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;

public abstract class ZetaNetworkHandler {
	public ZetaMessageSerializer serializer = new ZetaMessageSerializer();

	protected final Zeta zeta;
	protected final int protocolVersion;

	public ZetaNetworkHandler(Zeta zeta, int protocolVersion) {
		this.zeta = zeta;
		this.protocolVersion = protocolVersion;
	}

	public ZetaMessageSerializer getSerializer() {
		return serializer;
	}

	public void sendToPlayers(IZetaMessage msg, Iterable<ServerPlayer> players) {
		for(ServerPlayer player : players) sendToPlayer(msg, player);
	}

	public void sendToAllPlayers(IZetaMessage msg, MinecraftServer server) {
		sendToPlayers(msg, server.getPlayerList().getPlayers());
	}

	public abstract <T extends IZetaMessage> void register(Class<T> clazz, ZetaNetworkDirection dir);
	//TODO: BAD GARBAGE api
	public abstract <T extends ZetaHandshakeMessage> void registerLogin(Class<T> clazz, ZetaNetworkDirection dir, int id, boolean hasResponse, @Nullable Function<Boolean, List<Pair<String,T>>> loginPacketGenerators);

	public abstract void sendToPlayer(IZetaMessage msg, ServerPlayer player);
	public abstract void sendToServer(IZetaMessage msg);

	public abstract Packet<?> wrapInVanilla(IZetaMessage msg, ZetaNetworkDirection dir);
}
