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

/** Plays MC-001 Scene 007, "The Compass That Points Nowhere", at Orin's field table by the monument. */
class Scene007PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s007");

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

    boolean worldChange(String id) {
        return logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals(id));
    }

    void build(String stage) {
        assertEquals(stage, state.stage());
        assertEquals(SceneDef.CompletionType.BLOCKS, scene.stage(stage).completion());
        run(engine.handle(state, new StoryEvent.BlocksDone(stage)));
    }

    void examine(String clue) {
        run(engine.handle(state, new StoryEvent.ClueExamined(clue)));
    }

    @Test
    void fullPlaythrough() {
        assertEquals("mc001_s007", content.scene("mc001_s006").nextScene());
        assertEquals("northern_monument", scene.site());
        run(engine.startScene(state, "mc001_s007"));
        assertTrue(worldChange("mc001_s007_survey"), "the field table and survey stakes were not set out");
        walkInto("field_table");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s007_needles")));
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:compass")), "no anomaly compass");
        assertEquals("readings", state.stage());
        examine("survey_1");
        examine("pedestal"); // not yet
        examine("survey_2");
        assertEquals("readings", state.stage());
        examine("survey_3");
        assertEquals("compare", state.stage());
        examine("survey_map");
        assertEquals("pedestal", state.stage());
        examine("pedestal");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s007_projection")));
        assertTrue(state.isSceneComplete("mc001_s007"));
        assertTrue(state.hasFlag("mc001.anomaly_compass"));
        assertTrue(state.journal().contains("anomaly_compass"));

        assertEquals(List.of(
                "Orin: I've tried three compasses. Every single one is wrong.", "Steve: Maybe they're all broken.",
                "Orin: All three? At exactly the same time?", "Elara: Orin, what are they pointing toward?",
                "Orin: Whatever is buried beneath that structure.",
                "Steve: The signal isn't ahead of us.", "Orin: Then where?", "Steve: Below.", "Elara: Below the monument?",
                "Steve: Farther than that, I think.",
                "Orin: Did you both see that?", "Elara: Six points.", "Steve: Like the monument carving.",
                "Orin: We're going to need information from outside Firstbrook.",
                "Elara: Maybe the other kingdoms have seen these symbols."), spoken);
    }
}
