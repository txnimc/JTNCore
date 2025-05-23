package toni.jtn.foundation.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import toni.jtn.JTN;

public class PacketDistributor {
    public static void sendToAllPlayers(CustomPacketPayload customPacketPayload) {
        if (JTN.server == null)
            return;

        for (ServerPlayer player : PlayerLookup.all(JTN.server)) {
            sendToPlayer(player, customPacketPayload);
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
