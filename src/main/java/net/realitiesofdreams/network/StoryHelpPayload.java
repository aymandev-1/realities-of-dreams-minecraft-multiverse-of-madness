package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** Client to server: a button in the story help menu ("hint", "skip" or "checkpoint"). */
public record StoryHelpPayload(String action) implements CustomPacketPayload {
    public static final Type<StoryHelpPayload> TYPE = new Type<>(RealitiesOfDreams.id("story_help"));
    public static final StreamCodec<FriendlyByteBuf, StoryHelpPayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.<FriendlyByteBuf>cast().map(StoryHelpPayload::new, StoryHelpPayload::action);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
