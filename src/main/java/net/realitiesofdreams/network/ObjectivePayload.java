package net.realitiesofdreams.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/** The quest tracker. Empty text clears it. */
public record ObjectivePayload(String label, String title, String text, boolean hasTarget, double x, double y, double z,
                               String progress) implements CustomPacketPayload {
    public static final Type<ObjectivePayload> TYPE = new Type<>(RealitiesOfDreams.id("objective"));
    public static final StreamCodec<FriendlyByteBuf, ObjectivePayload> STREAM_CODEC = StreamCodec.of(ObjectivePayload::write, ObjectivePayload::read);

    public static ObjectivePayload clear() {
        return new ObjectivePayload("", "", "", false, 0, 0, 0, "");
    }

    private static void write(FriendlyByteBuf buf, ObjectivePayload p) {
        buf.writeUtf(p.label);
        buf.writeUtf(p.title);
        buf.writeUtf(p.text);
        buf.writeBoolean(p.hasTarget);
        buf.writeDouble(p.x);
        buf.writeDouble(p.y);
        buf.writeDouble(p.z);
        buf.writeUtf(p.progress);
    }

    private static ObjectivePayload read(FriendlyByteBuf buf) {
        return new ObjectivePayload(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readDouble(),
                buf.readDouble(), buf.readDouble(), buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
