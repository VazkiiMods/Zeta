package org.violetmoon.zeta.net2.event.load;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;
import org.violetmoon.zeta.net2.NetworkPhase;

public interface ZRegisterPayloadHandlers extends IZetaLoadEvent {
    <T> void registerServerboundPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase);
    <T> void registerClientboundPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase);
    <T> void registerBidirectionalPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase);
}
