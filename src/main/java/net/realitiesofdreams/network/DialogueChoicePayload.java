package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Client to server: the player picked a reply. */
public record DialogueChoicePayload(int session, int choice) implements CustomPacketPayload {
    public static final Type<DialogueChoicePayload> TYPE = new Type<>(RealitiesOfDreams.id("dialogue_choice"));
    public static final StreamCodec<FriendlyByteBuf, DialogueChoicePayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.session);
                buf.writeVarInt(p.choice);
            },
            buf -> new DialogueChoicePayload(buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
