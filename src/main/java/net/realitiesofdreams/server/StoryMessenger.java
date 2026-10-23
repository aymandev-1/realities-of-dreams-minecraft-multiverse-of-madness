package net.realitiesofdreams.server;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/** How the story talks to a player's client. Game tests swap in a recorder. */
public interface StoryMessenger {
    StoryMessenger NETWORK = PacketDistributor::sendToPlayer;

    void send(ServerPlayer player, CustomPacketPayload payload);
}
