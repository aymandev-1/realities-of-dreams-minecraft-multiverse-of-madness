package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** A short on-screen tip. Tokens like {forward} or {inventory} become the player's key names. */
public record HintPayload(String text, int ticks) implements CustomPacketPayload {
    public static final Type<HintPayload> TYPE = new Type<>(RealitiesOfDreams.id("hint"));
    public static final StreamCodec<FriendlyByteBuf, HintPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUtf(p.text);
                buf.writeVarInt(p.ticks);
            },
            buf -> new HintPayload(buf.readUtf(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
