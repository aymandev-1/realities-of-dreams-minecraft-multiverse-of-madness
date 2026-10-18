package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Client to server: a cinematic finished or was skipped. */
public record CinematicDonePayload(String id) implements CustomPacketPayload {
    public static final Type<CinematicDonePayload> TYPE = new Type<>(RealitiesOfDreams.id("cinematic_done"));
    public static final StreamCodec<FriendlyByteBuf, CinematicDonePayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.<FriendlyByteBuf>cast().map(CinematicDonePayload::new, CinematicDonePayload::id);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
