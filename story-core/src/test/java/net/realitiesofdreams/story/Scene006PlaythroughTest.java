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

/** Plays MC-001 Scene 006, "The Fractured Monument", at the monument's own site. */
class Scene006PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s006");

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
    void theMonumentIsItsOwnPlace() {
        assertEquals("northern_monument", scene.site());
        assertArrayEquals(new int[] {40, 108, -200}, scene.anchor());
        SceneEngine.BlockGoal stairs = SceneEngine.stageBlocks(scene.stage("entrance"));
        assertEquals("solid", stairs.mode());
        assertEquals(4, stairs.spots().size());
        assertArrayEquals(new int[] {0, 3, 9}, stairs.spots().get(3));
        SceneEngine.BlockGoal earth = SceneEngine.stageBlocks(scene.stage("uncover"));
        assertEquals("air", earth.mode());
        assertEquals(15, earth.spots().size());
        assertEquals(4, scene.clues().size());
    }

    @Test
    void fullPlaythrough() {
        assertEquals("mc001_s006", content.scene("mc001_s005").nextScene());
        run(engine.startScene(state, "mc001_s006"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("trail_start")),
                "the party was not taken to the mountain trail");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s006_approach")));
        assertEquals("to_courtyard", state.stage());
        walkInto("courtyard");
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:deepslate_bricks")));

        build("entrance");
        assertEquals("panels", state.stage());
        examine("panel_west");
        examine("fracture"); // not this stage's clue
        examine("panel_east");
        assertEquals("panels", state.stage());
        examine("panel_north");
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:spyglass")),
                "Orin did not lend his measuring glass");
        assertEquals("fracture", state.stage());
        examine("fracture");
        build("uncover");
        assertTrue(worldChange("mc001_s006_glow_on"), "the pillar's lines did not light");
        assertTrue(worldChange("mc001_s006_glow_off"), "the light did not fade");
        assertTrue(state.isSceneComplete("mc001_s006"));
        assertTrue(state.hasFlag("mc001.monument.recorded"));
        assertTrue(state.journal().contains("monument"));

        assertEquals(List.of(
                "Bram: That's bigger than it looked from the watchtower.", "Elara: The outer walls weren't visible yesterday.",
                "Orin: They've been buried for generations. The disturbance must've exposed them.",
                "Steve: Then the ground didn't just shake. Something underneath moved.", "Orin: That's what worries me.",
                "Steve: Six towers?", "Orin: Possibly. Or six settlements.",
                "Elara: Look at their shapes. That one resembles the towers of Eldervale.",
                "Bram: And that one looks like the old Stonecrown fortress.", "Steve: Why would somebody carve all six here?",
                "Orin: I don't know. But somebody wanted them remembered together.",
                "Elara: Steve! Move away!",
                "Steve: I didn't touch anything.", "Orin: I saw. It started before your hand reached the stone.",
                "Bram: Then we're not staying here after dark."), spoken);
    }
}
