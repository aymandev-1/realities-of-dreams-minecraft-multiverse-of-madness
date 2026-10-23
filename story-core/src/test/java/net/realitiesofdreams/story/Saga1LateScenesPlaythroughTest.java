package net.realitiesofdreams.story;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Plays MC-001 Scenes 012, 017, 018 and 019 from start to finish the way a player would: walking to
 * each objective, examining clues, building and clearing, watching every cinematic and dialogue.
 * Also checks the Scene 019 divergence is recorded as hidden story state and never shown.
 */
class Saga1LateScenesPlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);

    PlayerStoryState state = new PlayerStoryState();
    SceneDef scene;
    final List<Effect> log = new ArrayList<>();
    final List<String> spoken = new ArrayList<>();

    void run(List<Effect> effects) {
        Deque<Effect> queue = new ArrayDeque<>(effects);
        while (!queue.isEmpty()) {
            Effect e = queue.poll();
            log.add(e);
            if (e instanceof Effect.StartCinematic c) {
                for (var cue : content.cinematic(c.cinematicId()).cues()) queue.addAll(engine.applyActions(state, cue.actions()));
                queue.addAll(engine.handle(state, new StoryEvent.CinematicFinished(c.cinematicId())));
            } else if (e instanceof Effect.StartDialogue d) {
                DialogueSession s = new DialogueSession(content.dialogue(d.dialogueId()));
                queue.addAll(engine.applyActions(state, s.start()));
                while (!s.finished()) {
                    spoken.add(content.characters().displayName(s.current().speaker()) + ": " + s.current().text());
                    queue.addAll(engine.applyActions(state, s.awaitingChoice() ? s.choose(0) : s.advance()));
                }
                queue.addAll(engine.handle(state, new StoryEvent.DialogueFinished(d.dialogueId())));
            }
        }
    }

    void walkInto(String location) {
        run(engine.checkAreas(state, l -> l == scene.location(location)));
        run(engine.handle(state, new StoryEvent.Reached(location)));
    }

    /** Plays the scene to its end, solving each stage by its completion type. */
    void play(String sceneId) {
        scene = content.scene(sceneId);
        assertNotNull(scene, sceneId);
        run(engine.startScene(state, sceneId));
        for (int guard = 0; guard < 200 && !state.isSceneComplete(sceneId); guard++) {
            String stageId = state.stage();
            SceneDef.StageDef stage = scene.stage(stageId);
            assertNotNull(stage, "no stage " + stageId + " in " + sceneId);
            // Wander through the stage's areas first, as a player crossing the site would.
            for (SceneDef.AreaTrigger t : scene.areaTriggers())
                if (t.stages().contains(stageId)) run(engine.checkAreas(state, l -> l == scene.location(t.location())));
            String v = stage.completionValue();
            switch (stage.completion()) {
                case REACH -> walkInto(v);
                case CLUES, SEQUENCE -> {
                    for (String clue : v.split(",")) run(engine.handle(state, new StoryEvent.ClueExamined(clue.trim())));
                }
                case BLOCKS -> run(engine.handle(state, new StoryEvent.BlocksDone(stageId)));
                case ITEMS -> run(engine.handle(state, new StoryEvent.ItemsCollected(stageId)));
                case DEFEATED -> run(engine.handle(state, new StoryEvent.Defeated(stageId)));
                default -> fail(sceneId + " is stuck at stage " + stageId + " (" + stage.completion() + ")");
            }
            assertTrue(state.isSceneComplete(sceneId) || !stageId.equals(state.stage()), sceneId + " did not leave stage " + stageId);
        }
        assertTrue(state.isSceneComplete(sceneId), sceneId + " never finished");
    }

    boolean worldChange(String id) {
        return log.stream().anyMatch(e -> e instanceof Effect.WorldChange w && w.change().equals(id));
    }

    void assertInOrder(List<String> expected) {
        int from = 0;
        for (String line : expected) {
            int at = spoken.subList(from, spoken.size()).indexOf(line);
            assertTrue(at >= 0, "missing or out of order: " + line + "\nspoken: " + spoken);
            from += at + 1;
        }
    }

    @Test
    void scene012QueensSummons() {
        play("mc001_s012");
        assertTrue(state.hasFlag("mc001.s012.complete"));
        assertTrue(state.hasFlag("mc001.eldervale.audience"));
    }

    @Test
    void scene017TruthInTheFragments() {
        play("mc001_s017");
        assertTrue(worldChange("mc001_s017_skylight"));
        assertTrue(worldChange("mc001_s017_supplies"));
        assertTrue(state.hasFlag("mc001.expedition.assembled"));
    }

    @Test
    void scene018BeneathTheAncientStone() {
        assertEquals("mc001_s019", content.scene("mc001_s018").nextScene());
        play("mc001_s018");
        assertTrue(worldChange("mc001_s018_conduits"));
        assertTrue(worldChange("mc001_s018_door_open"));
        assertInOrder(List.of("Orin: Six symbols. Six records.", "Bram: Everybody stand back!",
                "Steve: What is that?", "Orin: Don't activate anything yet.", "Elara: Steve..."));
        assertTrue(spoken.contains("Elara: The air feels different down here."), "the descent conversation never played");
        assertTrue(state.hasFlag("mc001.s018.complete"));
    }

    @Test
    void scene018WrongLockResets() {
        scene = content.scene("mc001_s018");
        run(engine.startScene(state, "mc001_s018"));
        while (!"locks".equals(state.stage())) {
            String before = state.stage();
            SceneDef.StageDef stage = scene.stage(before);
            switch (stage.completion()) {
                case REACH -> walkInto(stage.completionValue());
                case CLUES -> { for (String c : stage.completionValue().split(",")) run(engine.handle(state, new StoryEvent.ClueExamined(c))); }
                case BLOCKS -> run(engine.handle(state, new StoryEvent.BlocksDone(before)));
                default -> fail("stuck at " + before);
            }
        }
        run(engine.handle(state, new StoryEvent.ClueExamined("s18_lock_eldervale")));
        run(engine.handle(state, new StoryEvent.ClueExamined("s18_lock_solkar"))); // wrong: Sylvaris comes next
        assertEquals("locks", state.stage());
        for (String c : List.of("eldervale", "sylvaris", "stonecrown", "solkar", "veyrath", "thalassia"))
            run(engine.handle(state, new StoryEvent.ClueExamined("s18_lock_" + c)));
        assertNotEquals("locks", state.stage(), "the right order did not open the door");
    }

    @Test
    void scene019RescueAndHiddenDivergence() {
        play("mc001_s019");
        assertInOrder(List.of(
                "Elara: Steve! The bridge is collapsing!",
                "Orin: The gateway's overloading! We need to evacuate!",
                "Steve: Elara, I'm coming!",
                "Elara: No! You'll get trapped too!",
                "Steve: I'm not leaving you!",
                "Steve: Take my hand!",
                "Steve: Those stars...",
                "Elara: Steve? Are you alright?",
                "Steve: Yeah... I think so.",
                "Liora: Either way, we cannot pretend this didn't happen."));
        for (String change : List.of("mc001_s019_rings", "mc001_s019_portal_on", "mc001_s019_bridge_falls",
                "mc001_s019_debris_1", "mc001_s019_debris_2", "mc001_s019_debris_3", "mc001_s019_portal_stars",
                "mc001_s019_echo_1", "mc001_s019_echo_4", "mc001_first_divergence", "mc001_s019_portal_off"))
            assertTrue(worldChange(change), "world change " + change + " never happened");
        assertStoryState();
        // The gateway closes without sending anyone anywhere: Saga 1 never leaves MC-001.
        assertTrue(scene.nextScene().startsWith("unreleased:"));
        assertTrue(log.stream().noneMatch(e -> e.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("universe")));

        // Replaying the scene keeps the record and does not duplicate or undo it.
        state.resetScene("mc001_s019", List.of("mc001.s019."));
        assertStoryState();
        log.clear();
        spoken.clear();
        play("mc001_s019");
        assertStoryState();
    }

    void assertStoryState() {
        for (String f : List.of("mc001_first_divergence", "elara_rescued", "gateway_temporal_branch_recorded", "first_gateway_closed"))
            assertTrue(state.hasFlag(f), "story state flag " + f + " not recorded");
        assertTrue(state.hasValue("primordial_steve_multiversal_travel_unlocked"));
        assertEquals(0, state.value("primordial_steve_multiversal_travel_unlocked"));
    }

    /** Nothing the player can read in Scenes 012 to 019 may reveal what Scene 019 set in motion. */
    @Test
    void noPlayerFacingTextRevealsTheSecret() throws Exception {
        Set<String> visibleKeys = Set.of("text", "title", "subtitle", "objective", "name", "lore");
        List<String> forbidden = List.of("herobrine", "timeline", "diverge", "temporal branch", "alternate", "aurelian",
                "last workshop", "another universe", "multivers");
        for (int n = 12; n <= 19; n++) {
            String file = String.format("/rod-story/scenes/mc001_s%03d.json", n);
            InputStream in = getClass().getResourceAsStream(file);
            if (in == null) continue; // a scene still being built in another branch
            List<String> texts = new ArrayList<>();
            collect(JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)), null, visibleKeys, texts);
            for (String t : texts)
                for (String word : forbidden)
                    assertFalse(t.toLowerCase(Locale.ROOT).contains(word), file + " shows the player \"" + t + "\"");
        }
    }

    private static void collect(JsonElement e, String key, Set<String> keys, List<String> out) {
        if (e.isJsonObject()) e.getAsJsonObject().entrySet().forEach(en -> collect(en.getValue(), en.getKey(), keys, out));
        else if (e.isJsonArray()) e.getAsJsonArray().forEach(x -> collect(x, key, keys, out));
        else if (e.isJsonPrimitive() && key != null && keys.contains(key)) out.add(e.getAsString());
    }
}
