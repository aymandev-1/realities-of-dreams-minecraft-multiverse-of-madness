package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

public record DialogueEndPayload(int session) implements CustomPacketPayload {
    public static final Type<DialogueEndPayload> TYPE = new Type<>(RealitiesOfDreams.id("dialogue_end"));
    public static final StreamCodec<FriendlyByteBuf, DialogueEndPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.<FriendlyByteBuf>cast().map(DialogueEndPayload::new, DialogueEndPayload::session);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
