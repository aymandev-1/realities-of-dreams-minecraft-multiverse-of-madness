package net.realitiesofdreams.story;

import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.story.world.Mc001Terrain;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Plays MC-001 Scene 003, "Elara's Northern Trail", the way the game drives it. */
class Scene003PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s003");

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
    void sceneTwoLeadsIntoSceneThree() {
        assertEquals("mc001_s003", content.scene("mc001_s002").nextScene());
        assertEquals("mc001_s004", scene.nextScene());
    }

    @Test
    void fullPlaythrough() {
        run(engine.startScene(state, "mc001_s003"));
        assertEquals("to_trail", state.stage());
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s003_trail")));
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("follow") && ((Effect.NpcCommand) e).character().equals("elara")));

        walkInto("trail_gate");
        assertTrue(logged(Effect.Say.class, e -> ((Effect.Say) e).character().equals("bram")));
        assertEquals("follow_trail", state.stage());

        // The tracks: cinematic, then the screenplay's conversation; Elara walks on with Steve.
        walkInto("footprints");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s003_tracks")));
        assertEquals("investigate", state.stage());

        // Travelling up the trail, the walking conversation starts once.
        walkInto("mid_trail");
        walkInto("mid_trail");
        assertEquals(1, spoken.stream().filter(l -> l.equals("Elara: I know this valley. I've crossed it since I was a child.")).count());

        state = PlayerStoryState.fromJson(state.toJson());
        run(engine.handle(state, new StoryEvent.ClueExamined("dropped_lantern")));
        run(engine.handle(state, new StoryEvent.ClueExamined("scout_pack")));
        assertEquals("markers", state.stage());
        run(engine.handle(state, new StoryEvent.ClueExamined("survey_marker")));
        assertEquals("markings", state.stage());

        walkInto("markings");
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s003_pulse_on")));
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s003_pulse_off")));
        assertTrue(state.isSceneComplete("mc001_s003"));
        assertTrue(state.hasFlag("mc001.s003.northern_trail_map"));
        assertTrue(state.journal().contains("northern_trail"));
        assertTrue(logged(Effect.GiveItem.class, e -> "Northern Trail Map".equals(((Effect.GiveItem) e).name())));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).subtitle().equals("Tracks in the Stone")));

        List<String> screenplay = List.of(
                "Elara: Look at this.", "Steve: A traveller?", "Elara: Maybe. But the tracks stop here.",
                "Steve: They could've climbed the ridge.", "Elara: Then where are the marks on the rock?",
                "Steve: You think somebody vanished?", "Elara: I think something happened here that neither of us understands.",
                "Elara: I know this valley. I've crossed it since I was a child.",
                "Steve: You make it sound like the valley betrayed you.", "Elara: Maybe that's exactly how it feels.",
                "Steve: Did you see that?", "Elara: Yes.", "Steve: They reacted to me.",
                "Elara: Or to the equipment you're carrying. We don't know yet.");
        assertEquals(screenplay, spoken.stream().filter(screenplay::contains).toList());
    }

    /** The trail locations stand on the authored hills, so arrows and reach checks match the ground. */
    @Test
    void trailLocationsSitOnTheGround() {
        for (String id : List.of("footprints", "elara_crouch", "lantern_spot", "rock_ledge", "markings")) {
            SceneDef.Location l = scene.location(id);
            int ground = Mc001Terrain.hillHeight((int) Math.floor(l.x()), (int) Math.floor(l.z()));
            if (id.equals("rock_ledge")) assertEquals(18, (int) l.y(), id + " is on top of the outcrop");
            else assertEquals(ground, (int) l.y(), id + " is not at ground level");
        }
        for (SceneDef.ClueDef c : scene.clues().values()) assertNotNull(content.dialogue(c.dialogue()), c.id());
    }
}
