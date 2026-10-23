package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** The ground trembles: a short camera shake during normal play. */
public record ShakePayload(float intensity, int ticks) implements CustomPacketPayload {
    public static final Type<ShakePayload> TYPE = new Type<>(RealitiesOfDreams.id("shake"));
    public static final StreamCodec<FriendlyByteBuf, ShakePayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeFloat(p.intensity);
                buf.writeVarInt(p.ticks);
            },
            buf -> new ShakePayload(buf.readFloat(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
