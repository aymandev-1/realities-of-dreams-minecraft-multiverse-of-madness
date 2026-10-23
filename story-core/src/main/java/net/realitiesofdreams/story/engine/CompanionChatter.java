package net.realitiesofdreams.story.engine;

import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.scene.SceneDef.ChatterDef;
import net.realitiesofdreams.story.state.PlayerStoryState;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Picks unprompted companion lines for the current situation. Each line is said once per
 * playthrough of the scene, so companions never repeat themselves.
 */
public final class CompanionChatter {
    private CompanionChatter() {}

    /**
     * @param context  "moving", "idle", "combat" or "near:&lt;location&gt;"
     * @param present  characters currently following or standing near the player
     */
    public static Optional<ChatterDef> pick(SceneDef scene, PlayerStoryState state, String context, Set<String> present, Random random) {
        if (scene == null) return Optional.empty();
        List<ChatterDef> pool = scene.chatter().stream()
                .filter(c -> c.context().equals(context))
                .filter(c -> c.stages().isEmpty() || c.stages().contains(state.stage()))
                .filter(c -> present.contains(c.character()))
                .filter(c -> !state.spokenChatter().contains(c.id()))
                .toList();
        if (pool.isEmpty()) return Optional.empty();
        ChatterDef chosen = pool.get(random.nextInt(pool.size()));
        state.spokenChatter().add(chosen.id());
        return Optional.of(chosen);
    }
}
