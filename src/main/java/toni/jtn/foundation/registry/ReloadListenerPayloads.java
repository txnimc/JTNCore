package toni.jtn.foundation.registry;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import toni.jtn.JTN;
import toni.jtn.foundation.codec.CodecProvider;
import toni.jtn.foundation.network.IPayloadContext;
import toni.jtn.foundation.network.PayloadProvider;

import java.util.List;
import java.util.Optional;

public class ReloadListenerPayloads {


    public static record Start(String path) implements CustomPacketPayload {

        public static final Type<Start> TYPE = new Type<>(JTN.location("reload_sync_start"));

        public static final StreamCodec<FriendlyByteBuf, Start> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Start::path,
            Start::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static class Provider implements PayloadProvider<Start> {

            @Override
            public Type<Start> getType() {
                return TYPE;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, Start> getCodec() {
                return CODEC;
            }

            @Override
            public void handle(Start msg, IPayloadContext ctx) {
                DynamicRegistry.SyncManagement.initSync(msg.path);
            }

            @Override
            public List<ConnectionProtocol> getSupportedProtocols() {
                return List.of(ConnectionProtocol.PLAY);
            }

            @Override
            public Optional<PacketFlow> getFlow() {
                return Optional.of(PacketFlow.CLIENTBOUND);
            }

            @Override
            public String getVersion() {
                return "1";
            }
        }
    }

    public static record Content<V extends CodecProvider<? super V>>(String path, ResourceLocation key, Either<V, ByteBuf> item) implements CustomPacketPayload {

        public static final Type<Content<?>> TYPE = new Type<>(JTN.location("reload_sync_content"));

        public static final StreamCodec<RegistryFriendlyByteBuf, Content<?>> CODEC = StreamCodec.of(Content::write, Content::read);

        public Content(String path, ResourceLocation key, V item) {
            this(path, key, Either.left(item));
        }

        public Content(String path, ResourceLocation key, ByteBuf buf) {
            this(path, key, Either.right(buf));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static <V extends CodecProvider<? super V>> void write(RegistryFriendlyByteBuf buf, Content<V> payload) {
            buf.writeUtf(payload.path, 50);
            buf.writeResourceLocation(payload.key);
            DynamicRegistry.SyncManagement.writeItem(payload.path, payload.item.orThrow(), buf);
        }

        /**
         * Reads a content payload. We defer deserialization of the underlying object, since it may depend on the state of
         * other registries that are being setup on the main thread.
         */
        public static <V extends CodecProvider<? super V>> Content<V> read(RegistryFriendlyByteBuf buf) {
            String path = buf.readUtf(50);
            ResourceLocation key = buf.readResourceLocation();

            int size = buf.writerIndex() - buf.readerIndex();
            ByteBuf itemBuf = Unpooled.buffer(size, size);
            buf.readBytes(itemBuf);
            return new Content<V>(path, key, itemBuf);
        }

        public static class Provider<V extends CodecProvider<? super V>> implements PayloadProvider<Content<?>> {

            @Override
            public Type<Content<?>> getType() {
                return TYPE;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, Content<?>> getCodec() {
                return CODEC;
            }

            @Override
            public void handle(Content<?> msg, IPayloadContext ctx) {
                RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(msg.item.right().get(), ctx.player().registryAccess());//, ConnectionType.NEOFORGE);

                try {
                    V value = DynamicRegistry.SyncManagement.readItem(msg.path, buf);
                    DynamicRegistry.SyncManagement.acceptItem(msg.path, msg.key, value);
                }
                catch (Exception ex) {
                    JTN.LOGGER.error("Failure when deserializing a dynamic registry object via network: Registry: {}, Object ID: {}", msg.path, msg.key);
                    throw ex;
                }
            }

            @Override
            public List<ConnectionProtocol> getSupportedProtocols() {
                return List.of(ConnectionProtocol.PLAY);
            }

            @Override
            public Optional<PacketFlow> getFlow() {
                return Optional.of(PacketFlow.CLIENTBOUND);
            }

            @Override
            public String getVersion() {
                return "1";
            }
        }
    }

    public static record End(String path) implements CustomPacketPayload {

        public static final Type<End> TYPE = new Type<>(JTN.location("reload_sync_end"));

        public static final StreamCodec<FriendlyByteBuf, End> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, End::path,
            End::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static class Provider implements PayloadProvider<End> {

            @Override
            public Type<End> getType() {
                return TYPE;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, End> getCodec() {
                return CODEC;
            }

            @Override
            public void handle(End msg, IPayloadContext ctx) {
                DynamicRegistry.SyncManagement.endSync(msg.path);
            }

            @Override
            public List<ConnectionProtocol> getSupportedProtocols() {
                return List.of(ConnectionProtocol.PLAY);
            }

            @Override
            public Optional<PacketFlow> getFlow() {
                return Optional.of(PacketFlow.CLIENTBOUND);
            }

            @Override
            public String getVersion() {
                return "1";
            }
        }
    }
}
