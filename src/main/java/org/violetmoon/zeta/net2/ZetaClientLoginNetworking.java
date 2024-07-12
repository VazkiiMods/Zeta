package org.violetmoon.zeta.net2;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.net2.packet.ZetaClientboundHandshakePacket;
import org.violetmoon.zeta.net2.packet.ZetaServerboundHandshakePacket;
import org.violetmoon.zeta.net2.packet.listener.ZetaClientLoginPacketListener;

public class ZetaClientLoginNetworking implements ZetaClientLoginPacketListener {
    private final Minecraft minecraft;
    private final Connection connection;

    public ZetaClientLoginNetworking(Minecraft minecraft, Connection connection) {
        this.minecraft = minecraft;
        this.connection = connection;
    }

    @Override
    public void handleHandshake(ZetaClientboundHandshakePacket packet) {
        if(packet.expectedLength == SyncedFlagHandler.expectedLength() && packet.expectedHash == SyncedFlagHandler.expectedHash())
            SyncedFlagHandler.receiveFlagInfoFromServer(packet.flags);
        connection.send(new ZetaServerboundHandshakePacket());
    }

    @Override
    public void onDisconnect(DisconnectionDetails p_350287_) {

    }

    @Override
    public boolean isAcceptingMessages() {
        return true;
    }
}
