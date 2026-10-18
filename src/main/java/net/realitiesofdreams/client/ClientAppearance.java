package net.realitiesofdreams.client;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.character.CharacterDef;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * How the server says players should be drawn during the campaign (Primordial Steve in MC-001).
 * Used by {@link net.realitiesofdreams.mixin.AbstractClientPlayerMixin}, so the override covers the
 * third-person model, first-person arms and armour layers alike. The account skin is never changed:
 * clearing an entry shows the player's own skin again.
 */
public final class ClientAppearance {
    private static final Map<UUID, PlayerSkin> skins = new HashMap<>();

    private ClientAppearance() {}

    static void set(UUID player, String character) {
        if (character.isEmpty()) {
            skins.remove(player);
            return;
        }
        Story.content().characters().get(character).filter(c -> c.texture() != null).ifPresent(c -> skins.put(player, skin(c)));
    }

    private static PlayerSkin skin(CharacterDef c) {
        ResourceLocation tex = ResourceLocation.parse(c.texture());
        return new PlayerSkin(tex, null, null, null, c.slim() ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE, true);
    }

    @Nullable
    public static PlayerSkin skinFor(UUID player) {
        return skins.isEmpty() ? null : skins.get(player);
    }

    public static boolean overridden(UUID player) {
        return skins.containsKey(player);
    }

    static void reset() {
        skins.clear();
    }
}
