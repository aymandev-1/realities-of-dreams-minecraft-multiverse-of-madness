package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Client to server: the player wants the next line now. */
public record DialogueSkipPayload(int session) implements CustomPacketPayload {
    public static final Type<DialogueSkipPayload> TYPE = new Type<>(RealitiesOfDreams.id("dialogue_skip"));
    public static final StreamCodec<FriendlyByteBuf, DialogueSkipPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.<FriendlyByteBuf>cast().map(DialogueSkipPayload::new, DialogueSkipPayload::session);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
