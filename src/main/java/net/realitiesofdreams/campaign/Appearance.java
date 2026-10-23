package net.realitiesofdreams.campaign;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.RodConfig;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.network.AppearancePayload;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Decides who is drawn as Primordial Steve and tells every client. The server is the authority, so
 * all players see the same thing; nothing about anyone's account or skin changes.
 *
 * Rules: in a story world the campaign's lead player is Primordial Steve while in MC-001; in an
 * ordinary world each player following their own Saga 1 story is Primordial Steve while in MC-001.
 * Everyone else, and everyone outside MC-001, keeps their own skin.
 */
public final class Appearance {
    public static final String PRIMORDIAL_STEVE = "primordial_steve";
    private static final Map<UUID, String> current = new HashMap<>();

    private Appearance() {}

    public static String desired(ServerPlayer p) {
        if (!RodConfig.primordialAppearance() || !p.level().dimension().equals(ModDimensions.MC_001)) return "";
        if (Campaign.isStoryWorld()) return Campaign.isLead(p) ? PRIMORDIAL_STEVE : "";
        PlayerStoryState s = ModAttachments.story(p);
        SceneDef active = Story.engine().activeScene(s);
        boolean saga1 = active != null ? active.saga() == 1 : s.completedScenes().stream().anyMatch(id -> id.startsWith("mc001_"));
        return saga1 ? PRIMORDIAL_STEVE : "";
    }

    /** Sends the player's appearance to everyone if it changed. */
    public static void refresh(ServerPlayer p) {
        String want = desired(p);
        if (want.equals(current.getOrDefault(p.getUUID(), ""))) return;
        if (want.isEmpty()) current.remove(p.getUUID());
        else current.put(p.getUUID(), want);
        PacketDistributor.sendToAllPlayers(new AppearancePayload(p.getUUID(), want));
    }

    /** A joining player learns how everyone else looks. */
    public static void onLogin(ServerPlayer p) {
        current.forEach((id, character) -> {
            if (!id.equals(p.getUUID())) PacketDistributor.sendToPlayer(p, new AppearancePayload(id, character));
        });
        current.remove(p.getUUID());
        refresh(p);
    }

    public static void onLogout(ServerPlayer p) {
        if (current.remove(p.getUUID()) != null) PacketDistributor.sendToAllPlayers(new AppearancePayload(p.getUUID(), ""));
    }

    public static void reset() {
        current.clear();
    }
}
