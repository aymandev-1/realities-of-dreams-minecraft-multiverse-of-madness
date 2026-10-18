package net.realitiesofdreams.story;

import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Plays MC-001 Scene 004, "The Craftsman's Warning": the survey kit is real crafting. */
class Scene004PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s004");

    PlayerStoryState state = new PlayerStoryState();
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

    boolean logged(Class<?> type, java.util.function.Predicate<Object> test) {
        return log.stream().anyMatch(e -> type.isInstance(e) && test.test(e));
    }

    @Test
    void theSurveyKitIsRealItems() {
        SceneDef.StageDef kit = scene.stage("kit");
        assertEquals(SceneDef.CompletionType.ITEMS, kit.completion());
        List<SceneEngine.ItemGoal> goals = SceneEngine.stageItems(kit);
        assertEquals(List.of(new SceneEngine.ItemGoal("minecraft:compass", 1), new SceneEngine.ItemGoal("minecraft:lead", 1),
                new SceneEngine.ItemGoal("minecraft:white_dye", 1), new SceneEngine.ItemGoal("minecraft:torch", 8),
                new SceneEngine.ItemGoal("minecraft:iron_pickaxe", 1)), goals);
    }

    @Test
    void fullPlaythrough() {
        assertEquals("mc001_s004", content.scene("mc001_s003").nextScene());
        run(engine.startScene(state, "mc001_s004"));
        walkInto("workshop_inside");
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s004_sketch")), "Elara's sketch was not laid out");
        assertEquals("kit", state.stage());
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s004_supplies")));

        // Progress is whatever the game last counted; another stage's event cannot finish the kit.
        state.setValue(SceneEngine.ITEMS_PROGRESS, 3);
        assertEquals("3/5", ((Effect.ShowObjective) engine.objective(state, scene, scene.stage("kit"))).progress());
        run(engine.handle(state, new StoryEvent.ItemsCollected("some_other_stage")));
        assertEquals("kit", state.stage());

        // Orin's warning plays once at the workbench.
        walkInto("workbench_front");
        walkInto("workbench_front");
        assertEquals(1, spoken.stream().filter(l -> l.equals("Orin: This time, don't.")).count());

        run(engine.handle(state, new StoryEvent.ItemsCollected("kit")));
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s004_compass")));
        assertTrue(state.isSceneComplete("mc001_s004"));
        assertTrue(state.hasFlag("mc001.orin.adviser") && state.hasFlag("mc001.unlock.repair_station"));
        assertTrue(state.journal().contains("survey_kit"));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).subtitle().equals("Tools for the Unknown")));

        assertEquals(List.of(
                "Orin: Before you ask, no, I haven't worked out what happened.", "Steve: We haven't asked yet.",
                "Orin: Your faces asked for you.", "Elara: We found markings near the northern path.", "Orin: Markings?",
                "Orin: Where did you find that?", "Steve: On a rock near the ridge.", "Orin: That's not a natural crack.",
                "Orin: You can break most stone with a good pickaxe.", "Steve: That's usually the idea.", "Orin: This time, don't.",
                "Elara: He's serious, Steve.", "Orin: Very serious. I want measurements before somebody destroys the evidence.",
                "Steve: That's not normal.", "Orin: Glad we've established that."), spoken);
    }
}
