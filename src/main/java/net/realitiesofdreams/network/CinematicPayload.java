package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Plays a cinematic from the shared story content, positioned relative to the scene anchor. */
public record CinematicPayload(String id, int anchorX, int anchorY, int anchorZ) implements CustomPacketPayload {
    public static final Type<CinematicPayload> TYPE = new Type<>(RealitiesOfDreams.id("cinematic"));
    public static final StreamCodec<FriendlyByteBuf, CinematicPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUtf(p.id);
                buf.writeVarInt(p.anchorX);
                buf.writeVarInt(p.anchorY);
                buf.writeVarInt(p.anchorZ);
            },
            buf -> new CinematicPayload(buf.readUtf(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
