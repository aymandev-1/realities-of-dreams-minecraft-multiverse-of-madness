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

/** Plays MC-001 Scene 009, "The Empty Campsite": the clues, the tremor and the Sylvaris post. */
class Scene009PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s009");

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
        assertEquals("mc001_s009", content.scene("mc001_s008").nextScene());
        assertEquals(content.scene("mc001_s008").site(), scene.site());
        run(engine.startScene(state, "mc001_s009"));
        walkInto("camp_edge");
        assertEquals("investigate", state.stage());
        for (String clue : List.of("lantern", "erased_map", "journal", "tracks")) examine(clue);
        examine("sylvaris_post"); // not yet
        assertEquals("investigate", state.stage());
        examine("supply_chest");
        assertTrue(worldChange("mc001_s009_lantern"), "the lantern did not roll");
        assertEquals("to_post", state.stage());
        examine("sylvaris_post");
        assertTrue(state.isSceneComplete("mc001_s009"));
        assertTrue(state.journal().contains("missing_expedition"));
        assertEquals(List.of(
                "Bram: Nobody here.", "Elara: The fire's cold, but these supplies haven't been here long.",
                "Steve: They left in a hurry.", "Bram: Or somebody made them leave.",
                "Elara: These people were mapping the same markings we found.", "Steve: How do you know?",
                "Elara: Look at this drawing.", "Bram: Who sent them?", "Steve: The last page is missing.",
                "Elara: Maybe they carried it with them.",
                "Bram: There it is again.", "Steve: The same signal.", "Elara: And it's getting stronger.",
                "Elara: Sylvaris.", "Steve: The forest civilisation?", "Elara: Someone from Sylvaris knows about this site."), spoken);
    }
}
