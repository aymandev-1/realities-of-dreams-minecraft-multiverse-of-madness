package net.realitiesofdreams.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Client to server: the loading screen has closed and the player can see the world. */
public record ClientReadyPayload() implements CustomPacketPayload {
    public static final Type<ClientReadyPayload> TYPE = new Type<>(RealitiesOfDreams.id("client_ready"));
    public static final StreamCodec<ByteBuf, ClientReadyPayload> STREAM_CODEC = StreamCodec.unit(new ClientReadyPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
