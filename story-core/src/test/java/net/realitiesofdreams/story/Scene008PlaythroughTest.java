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

/** Plays MC-001 Scene 008, "Across Awakening Valley": the bridge, the camp, the markers and the overlook. */
class Scene008PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s008");

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
        assertEquals("mc001_s008", content.scene("mc001_s007").nextScene());
        assertEquals("valley_crossing", scene.site());
        run(engine.startScene(state, "mc001_s008"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("trail_start")));
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s008_valley")));
        assertEquals("to_bridge", state.stage());
        walkInto("bridge_west");
        assertTrue(worldChange("mc001_s008_collapse"), "the bridge did not give way");
        assertEquals("rescue", state.stage());
        examine("crate");
        assertTrue(worldChange("mc001_s008_crate_saved"));
        assertEquals(3, SceneEngine.stageBlocks(scene.stage("bridge")).spots().size());
        build("bridge");
        walkInto("east_bank");
        assertEquals("minecraft:campfire", SceneEngine.stageBlocks(scene.stage("camp")).mode());
        build("camp");
        examine("marker_1");
        examine("marker_2");
        examine("marker_3");
        assertEquals("provisions", state.stage());
        assertEquals(SceneDef.CompletionType.ITEMS, scene.stage("provisions").completion());
        run(engine.handle(state, new StoryEvent.ItemsCollected("provisions")));
        walkInto("overlook_top");
        assertTrue(state.isSceneComplete("mc001_s008"));
        assertTrue(state.hasFlag("mc001.valley.camp"));
        assertTrue(state.journal().contains("valley"));
        assertEquals(21, spoken.size());
        assertEquals("Elara: Beautiful, isn't it?", spoken.get(0));
        assertEquals("Steve: I said it would've worked.", spoken.get(17));
        assertEquals("Steve: Let's find out.", spoken.get(20));
    }
}
