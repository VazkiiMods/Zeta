package org.violetmoon.zeta.net2.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.net2.packet.listener.ZetaClientLoginPacketListener;

import java.util.BitSet;

public class ZetaClientboundHandshakePacket implements Packet<ZetaClientLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ZetaClientboundHandshakePacket> STREAM_CODEC = Packet.codec(
            ZetaClientboundHandshakePacket::write, ZetaClientboundHandshakePacket::new
    );
    public BitSet flags;
    public int expectedLength;
    public int expectedHash;
    public ZetaClientboundHandshakePacket() {
        flags = SyncedFlagHandler.compileFlagInfo();
        expectedLength = SyncedFlagHandler.expectedLength();
        expectedHash = SyncedFlagHandler.expectedHash();
    }

    private ZetaClientboundHandshakePacket(FriendlyByteBuf friendlyByteBuf) {
        this.flags = friendlyByteBuf.readBitSet();
        this.expectedLength = friendlyByteBuf.readInt();
        this.expectedHash = friendlyByteBuf.readInt();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(expectedHash);
        friendlyByteBuf.writeInt(expectedLength);
        friendlyByteBuf.writeBitSet(flags);
    }

    public PacketType<ZetaClientboundHandshakePacket> type() {
        return new PacketType<>(PacketFlow.CLIENTBOUND, ResourceLocation.fromNamespaceAndPath("zeta", "login_flag"));
    }

    public void handle(ZetaClientLoginPacketListener packetListener) {
        if(expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
            SyncedFlagHandler.receiveFlagInfoFromServer(flags);

        //network.send(serverboundLoginFlag);
    }
}
