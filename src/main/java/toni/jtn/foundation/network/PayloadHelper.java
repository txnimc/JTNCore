package toni.jtn.foundation.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Preconditions;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import toni.jtn.JTN;
import toni.jtn.foundation.registry.ReloadListenerPayloads;

public class PayloadHelper {

    private static final Map<CustomPacketPayload.Type<?>, PayloadProvider<?>> ALL_PROVIDERS = new HashMap<>();
    private static boolean locked = false;


    public static <T> CompletableFuture<T> guard(CompletableFuture<T> future, ResourceLocation payloadId) {
        return future.exceptionally(
            ex -> {
                JTN.LOGGER.error("Failed to process a synchronized task of the payload: %s".formatted(payloadId), ex);
                return null;
            });
    }

    /**
     * Registers a payload using {@link PayloadProvider}.
     *
     * @param prov    An instance of the payload provider.
     */
    public static <T extends CustomPacketPayload> void registerPayload(PayloadProvider<T> prov) {
        Preconditions.checkNotNull(prov);
        synchronized (ALL_PROVIDERS) {
            if (locked) throw new UnsupportedOperationException("Attempted to register a payload provider after registration has finished.");
            if (ALL_PROVIDERS.containsKey(prov.getType())) throw new UnsupportedOperationException("Attempted to register payload provider with duplicate ID: " + prov.getType().id());
            ALL_PROVIDERS.put(prov.getType(), prov);
        }
    }


    #if NEO
    @SubscribeEvent
    public void registerProviders(RegisterPayloadHandlersEvent event) {
         synchronized (ALL_PROVIDERS) {
            for (PayloadProvider prov : ALL_PROVIDERS.values()) {
                NetworkRegistry.register(prov.getType(), prov.getCodec(), new PayloadHandler(prov), prov.getSupportedProtocols(), prov.getFlow(), prov.getVersion(), prov.isOptional());
            }
            locked = true;
        }
    }
    }
    #else
    public static void registerProviders() {
        synchronized (ALL_PROVIDERS) {
            for (PayloadProvider prov : ALL_PROVIDERS.values()) {
                #if NEO
                NetworkRegistry.register(prov.getType(), prov.getCodec(), new PayloadHandler(prov), prov.getSupportedProtocols(), prov.getFlow(), prov.getVersion(), prov.isOptional());
                #else

                if (prov.getFlow().isEmpty() || prov.getFlow().get() == PacketFlow.CLIENTBOUND) {
                    PayloadTypeRegistry.playS2C().register(prov.getType(), prov.getCodec());
                }

                if (prov.getFlow().isEmpty() || prov.getFlow().get() == PacketFlow.SERVERBOUND) {
                    PayloadTypeRegistry.playC2S().register(prov.getType(), prov.getCodec());
                }

                ClientPlayNetworking.registerGlobalReceiver(prov.getType(), (payload, context) -> {
                    context.client().execute(() -> {
                        new PayloadHandler(prov).handle(payload, new IPayloadContext.Client(context));
                    });
                });
                #endif
            }
            locked = true;
        }
    }

    public void registerProvidersClient() {
        synchronized (ALL_PROVIDERS) {
            for (PayloadProvider prov : ALL_PROVIDERS.values()) {
                ClientPlayNetworking.registerGlobalReceiver(prov.getType(), (payload, context) -> {
                    context.client().execute(() -> {
                        new PayloadHandler(prov).handle(payload, new IPayloadContext.Client(context));
                    });
                });
            }
            locked = true;
        }
    }
    #endif

    private static class PayloadHandler<T extends CustomPacketPayload> implements IPayloadHandler<T> {

        private PayloadProvider<T> provider;
        private Optional<PacketFlow> flow;
        private List<ConnectionProtocol> protocols;

        private PayloadHandler(PayloadProvider<T> provider) {
            this.provider = provider;
            this.flow = provider.getFlow();
            this.protocols = provider.getSupportedProtocols();
            Preconditions.checkArgument(!this.protocols.isEmpty(), "The payload registration for " + provider.getType().id() + " must specify at least one valid protocol.");
        }

        @Override
        public void handle(T payload, IPayloadContext context) {
            if (this.flow.isPresent() && this.flow.get() != context.flow()) {
                JTN.LOGGER.error("Received a payload {} on the incorrect side.", payload.type().id());
                return;
            }

            if (!this.protocols.contains(context.protocol())) {
                JTN.LOGGER.error("Received a payload {} on the incorrect protocol.", payload.type().id());
                return;
            }

            switch (provider.getHandlerThread()) {
                case MAIN -> context.enqueueWork(() -> this.provider.handle(payload, context));
                case NETWORK -> this.provider.handle(payload, context);
            }
        }
    }

}