package toni.jtn.foundation.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Callback for handling custom packets.
 *
 * @param <T> The type of payload.
 */
@FunctionalInterface
public interface IPayloadHandler<T extends CustomPacketPayload> {
    /**
     * Handles the payload with the supplied context.
     */
    void handle(T payload, IPayloadContext context);
}