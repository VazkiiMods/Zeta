package org.violetmoon.zeta.net2.packet.listener;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.game.ServerPacketListener;
import org.violetmoon.zeta.net2.packet.ZetaServerboundHandshakePacket;

public interface ZetaServerLoginPacketListener extends ServerPacketListener {
    @Override
    default ConnectionProtocol protocol() {
        return ConnectionProtocol.LOGIN;
    }

    void handleHandshake(ZetaServerboundHandshakePacket packet);
}
