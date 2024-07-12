package org.violetmoon.zeta.net2.packet.listener;

import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import org.violetmoon.zeta.net2.packet.ZetaClientboundHandshakePacket;

public interface ZetaClientLoginPacketListener extends ClientboundPacketListener {
    @Override
    default ConnectionProtocol protocol() {
        return ConnectionProtocol.LOGIN;
    }

    void handleHandshake(ZetaClientboundHandshakePacket packet);
}
