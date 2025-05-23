package toni.jtn.foundation.network;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.ApiStatus;


public interface IPayloadContext {
    Player player();

    PacketSender responseSender();

    ConnectionProtocol protocol();

    PacketFlow flow();

    CompletableFuture<Void> enqueueWork(Runnable task);

    <T> CompletableFuture<T> enqueueWork(Supplier<T> task);



    public record Server(ServerPlayNetworking.Context context) implements IPayloadContext {
        @Override
        public Player player() { return context.player(); }

        @Override
        public PacketSender responseSender() { return context.responseSender();}

        @Override
        public ConnectionProtocol protocol() {
            return ConnectionProtocol.PLAY;
        }

        @Override
        public PacketFlow flow() {
            return PacketFlow.SERVERBOUND;
        }

        @Override
        public CompletableFuture<Void> enqueueWork(Runnable task) {
            return server().submit(task);
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
            return server().submit(task);
        }

        public MinecraftServer server() { return context.server(); }
    }



    public record Client(ClientPlayNetworking.Context context) implements IPayloadContext {
        @Override
        public Player player() { return context.player(); }

        @Override
        public PacketSender responseSender() { return context.responseSender();}

        @Override
        public ConnectionProtocol protocol() {
            return ConnectionProtocol.PLAY;
        }

        @Override
        public PacketFlow flow() {
            return PacketFlow.CLIENTBOUND;
        }

        @Override
        public CompletableFuture<Void> enqueueWork(Runnable task) {
            return client().submit(task);
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
            return client().submit(task);
        }

        public Minecraft client() { return context.client(); }
    }
}
