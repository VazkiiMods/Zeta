package org.violetmoon.zetaimplforge.net2.event.load;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.net2.NetworkPhase;
import org.violetmoon.zeta.net2.event.load.ZRegisterPayloadHandlers;
import org.violetmoon.zetaimplforge.net2.handlers.ZetaPayloadHandler;

public class ForgeZRegisterPayloadHandlers implements ZRegisterPayloadHandlers {
    private final RegisterPayloadHandlersEvent e;

    public ForgeZRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        this.e = event;
    }

    @Override
    public <T> void registerServerboundPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase) {
        if (handler instanceof ZetaPayloadHandler zetaPayloadHandler) {
            final PayloadRegistrar registrar = e.registrar("1");
            switch (phase) {
                case PLAY: {
                   registrar.playToServer(type, codec, zetaPayloadHandler);
                }
                case LOGIN: {
                    registrar.configurationToServer(type, codec, zetaPayloadHandler);
                }
                case ANY: {
                    registrar.commonToServer(type, codec, zetaPayloadHandler);
                }
            }
        } else {
            Zeta.GLOBAL_LOG.fatal("THE PACKETS HAVE FAILED, OH GOD. PLEASE REPLACE ME WITH AN ACTUAL ERROR MESSAGE"); //todo: Replace this with an actual error message
        }
    }

    @Override
    public <T> void registerClientboundPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase) {
        if (handler instanceof ZetaPayloadHandler zetaPayloadHandler) {
            final PayloadRegistrar registrar = e.registrar("1");
            switch (phase) {
                case PLAY: {
                    registrar.playToClient(type, codec, zetaPayloadHandler);
                }
                case LOGIN: {
                    registrar.configurationToClient(type, codec, zetaPayloadHandler);
                }
                case ANY: {
                    registrar.commonToClient(type, codec, zetaPayloadHandler);
                }
            }
        } else {
            Zeta.GLOBAL_LOG.fatal("THE PACKETS HAVE FAILED, OH GOD. PLEASE REPLACE ME WITH AN ACTUAL ERROR MESSAGE"); //todo: Replace this with an actual error message
        }
    }

    @Override
    public <T> void registerBidirectionalPayload(CustomPacketPayload.Type type, StreamCodec codec, T handler, NetworkPhase phase) {
        if (handler instanceof ZetaPayloadHandler zetaPayloadHandler) {
            final PayloadRegistrar registrar = e.registrar("1");
            switch (phase) {
                case PLAY: {
                    registrar.playBidirectional(type, codec, zetaPayloadHandler);
                }
                case LOGIN: {
                    registrar.configurationBidirectional(type, codec, zetaPayloadHandler);
                }
                case ANY: {
                    registrar.commonBidirectional(type, codec, zetaPayloadHandler);
                }
            }
        } else {
            Zeta.GLOBAL_LOG.fatal("THE PACKETS HAVE FAILED, OH GOD. PLEASE REPLACE ME WITH AN ACTUAL ERROR MESSAGE"); //todo: Replace this with an actual error message
        }
    }
}
