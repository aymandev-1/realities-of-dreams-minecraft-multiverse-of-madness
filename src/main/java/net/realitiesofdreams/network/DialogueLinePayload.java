package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

import java.util.ArrayList;
import java.util.List;

/** One spoken line. ambient lines (companion chatter, greetings in passing) never block the player. */
public record DialogueLinePayload(int session, String speakerId, String speakerName, String text, int ticks,
                                  List<String> choices, boolean ambient) implements CustomPacketPayload {
    public static final Type<DialogueLinePayload> TYPE = new Type<>(RealitiesOfDreams.id("dialogue_line"));
    public static final StreamCodec<FriendlyByteBuf, DialogueLinePayload> STREAM_CODEC = StreamCodec.of(DialogueLinePayload::write, DialogueLinePayload::read);

    private static void write(FriendlyByteBuf buf, DialogueLinePayload p) {
        buf.writeVarInt(p.session);
        buf.writeUtf(p.speakerId);
        buf.writeUtf(p.speakerName);
        buf.writeUtf(p.text);
        buf.writeVarInt(p.ticks);
        buf.writeVarInt(p.choices.size());
        for (String c : p.choices) buf.writeUtf(c);
        buf.writeBoolean(p.ambient);
    }

    private static DialogueLinePayload read(FriendlyByteBuf buf) {
        int session = buf.readVarInt();
        String id = buf.readUtf(), name = buf.readUtf(), text = buf.readUtf();
        int ticks = buf.readVarInt();
        int n = buf.readVarInt();
        List<String> choices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) choices.add(buf.readUtf());
        return new DialogueLinePayload(session, id, name, text, ticks, List.copyOf(choices), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
