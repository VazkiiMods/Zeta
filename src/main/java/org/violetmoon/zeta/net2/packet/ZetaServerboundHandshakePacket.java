package org.violetmoon.zeta.net2.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.net2.packet.listener.ZetaServerLoginPacketListener;

import java.util.BitSet;

public class ZetaServerboundHandshakePacket implements Packet<ZetaServerLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ZetaServerboundHandshakePacket> STREAM_CODEC = Packet.codec(
            ZetaServerboundHandshakePacket::write, ZetaServerboundHandshakePacket::new
    );
    public BitSet flags;
    public int expectedLength;
    public int expectedHash;
    public ZetaServerboundHandshakePacket() {
        flags = SyncedFlagHandler.compileFlagInfo();
        expectedLength = SyncedFlagHandler.expectedLength();
        expectedHash = SyncedFlagHandler.expectedHash();
    }

    private ZetaServerboundHandshakePacket(FriendlyByteBuf friendlyByteBuf) {
        this.flags = friendlyByteBuf.readBitSet();
        this.expectedLength = friendlyByteBuf.readInt();
        this.expectedHash = friendlyByteBuf.readInt();
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(expectedHash);
        friendlyByteBuf.writeInt(expectedLength);
        friendlyByteBuf.writeBitSet(flags);
    }

    public PacketType<ZetaServerboundHandshakePacket> type() {
        return new PacketType<>(PacketFlow.SERVERBOUND, ResourceLocation.fromNamespaceAndPath("zeta", "login_flag"));
    }

    @Override
    public void handle(ZetaServerLoginPacketListener packetListener) {
        packetListener.handleHandshake(this);
    }
}
