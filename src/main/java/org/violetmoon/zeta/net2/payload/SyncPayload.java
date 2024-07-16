package org.violetmoon.zeta.net2.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.Zeta;

public record SyncPayload(byte[] flags, int expectedLength, int expectedHash) implements ZetaPacketPayload {
    public static final CustomPacketPayload.Type<SyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Zeta.ZETA_ID, "handshake_payload"));
    public static final StreamCodec<ByteBuf, SyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY, SyncPayload::flags,
            ByteBufCodecs.INT, SyncPayload::expectedLength,
            ByteBufCodecs.INT, SyncPayload::expectedHash,
            SyncPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
