package net.realitiesofdreams.story.engine;

import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.scene.SceneDef.TriggerDef;
import net.realitiesofdreams.story.state.PlayerStoryState;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Decides which character, if any, should start talking to the player on their own.
 * Story-critical conversations win over ambient ones; then priority; then the closest character.
 */
public final class ProximityDirector {

    /** A story character near the player. */
    public record NearbyCharacter(String characterId, double distance, boolean canSee) {}

    /** approach = the character should walk up to the player before speaking. */
    public record Decision(TriggerDef trigger, NearbyCharacter npc, boolean approach) {}

    private ProximityDirector() {}

    public static Optional<Decision> choose(SceneDef scene, PlayerStoryState state, List<NearbyCharacter> nearby) {
        if (scene == null) return Optional.empty();
        return scene.triggers().stream()
                .filter(t -> eligible(t, state))
                .flatMap(t -> nearby.stream()
                        .filter(n -> n.characterId().equals(t.character()) && n.canSee() && n.distance() <= t.radius())
                        .map(n -> new Decision(t, n, t.approach() && n.distance() > t.talkRadius())))
                .min(Comparator.<Decision>comparingInt(d -> d.trigger().ambient() ? 1 : 0)
                        .thenComparing(d -> -d.trigger().priority())
                        .thenComparingDouble(d -> d.npc().distance()));
    }

    public static boolean eligible(TriggerDef t, PlayerStoryState state) {
        if (t.once() && state.triggerFired(t.id())) return false;
        if (!t.stages().isEmpty() && !t.stages().contains(state.stage())) return false;
        for (String f : t.requires()) if (!state.hasFlag(f)) return false;
        for (String f : t.forbids()) if (state.hasFlag(f)) return false;
        return true;
    }
}
