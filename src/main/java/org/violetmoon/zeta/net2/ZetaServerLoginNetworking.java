package org.violetmoon.zeta.net2;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.net2.packet.ZetaServerboundHandshakePacket;
import org.violetmoon.zeta.net2.packet.listener.ZetaServerLoginPacketListener;

public class ZetaServerLoginNetworking implements ZetaServerLoginPacketListener {
    private final MinecraftServer server;
    private final Connection connection;
    private final ServerPlayer player;

    public ZetaServerLoginNetworking(MinecraftServer server, Connection connection, ServerPlayer player) {
        this.server = server;
        this.connection = connection;
        this.player = player;
    }

    @Override
    public void handleHandshake(ZetaServerboundHandshakePacket packet) {
        if(packet.expectedLength == SyncedFlagHandler.expectedLength() && packet.expectedHash == SyncedFlagHandler.expectedHash())
            SyncedFlagHandler.receiveFlagInfoFromPlayer(player, packet.flags);
    }

    @Override
    public void onDisconnect(DisconnectionDetails p_350287_) {

    }

    @Override
    public boolean isAcceptingMessages() {
        return true;
    }
}
