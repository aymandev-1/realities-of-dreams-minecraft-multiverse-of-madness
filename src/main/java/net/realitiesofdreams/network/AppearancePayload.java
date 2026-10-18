package net.realitiesofdreams.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.realitiesofdreams.RealitiesOfDreams;

/**
 * Server to client: how a player should be drawn. {@code character} is a registry character id
 * (Primordial Steve during the MC-001 campaign), or empty for the player's own skin. Purely visual;
 * account skins are never changed.
 */
public record AppearancePayload(java.util.UUID player, String character) implements CustomPacketPayload {
    public static final Type<AppearancePayload> TYPE = new Type<>(RealitiesOfDreams.id("appearance"));
    public static final StreamCodec<FriendlyByteBuf, AppearancePayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                UUIDUtil.STREAM_CODEC.encode(buf, p.player);
                buf.writeUtf(p.character);
            },
            buf -> new AppearancePayload(UUIDUtil.STREAM_CODEC.decode(buf), buf.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
