package net.realitiesofdreams.story;

import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.ProximityDirector;
import net.realitiesofdreams.story.engine.ProximityDirector.NearbyCharacter;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Plays MC-001 Scene 002, "The Warning from the North", straight on from the end of Scene 001. */
class Scene002PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s002");

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

    Optional<String> approach(String character, double distance) {
        Optional<ProximityDirector.Decision> d = ProximityDirector.choose(scene, state, List.of(new NearbyCharacter(character, distance, true)));
        d.ifPresent(dec -> {
            state.markTriggerFired(dec.trigger().id());
            run(List.of(new Effect.StartDialogue(dec.trigger().dialogue())));
        });
        return d.map(dec -> dec.trigger().id());
    }

    boolean logged(Class<?> type, java.util.function.Predicate<Object> test) {
        return log.stream().anyMatch(e -> type.isInstance(e) && test.test(e));
    }

    Effect.ShowObjective lastObjective() {
        for (int i = log.size() - 1; i >= 0; i--) if (log.get(i) instanceof Effect.ShowObjective o) return o;
        return null;
    }

    @Test
    void sceneOneLeadsIntoSceneTwo() {
        assertEquals("mc001_s002", content.scene("mc001_s001").nextScene());
        assertEquals("mc001_s003", scene.nextScene());
        assertEquals(2, scene.sceneNumber());
    }

    @Test
    void fullPlaythrough() {
        run(engine.startScene(state, "mc001_s002"));
        assertEquals("warning", state.stage());
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("approach") && ((Effect.NpcCommand) e).character().equals("elder_rowan")));

        // Rowan comes over and starts talking without being clicked.
        ProximityDirector.Decision far = ProximityDirector.choose(scene, state, List.of(new NearbyCharacter("elder_rowan", 20, true))).orElseThrow();
        assertTrue(far.approach(), "Rowan walks over first");
        assertEquals(Optional.of("rowan_warning"), approach("elder_rowan", 3));
        assertEquals("inspect", state.stage());
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s002_aftermath")));
        assertEquals("0/3", lastObjective().progress());

        // Three physical clues; each one is examined once.
        run(engine.handle(state, new StoryEvent.ClueExamined("cracked_wall")));
        assertEquals("1/3", lastObjective().progress());
        run(engine.handle(state, new StoryEvent.ClueExamined("cracked_wall")));
        assertEquals("1/3", lastObjective().progress());

        // Saving mid-investigation keeps the clue found.
        state = PlayerStoryState.fromJson(state.toJson());
        assertTrue(state.cluesFound().contains("cracked_wall"));

        run(engine.handle(state, new StoryEvent.ClueExamined("spinning_compass")));
        run(engine.handle(state, new StoryEvent.ClueExamined("tower_report")));
        assertEquals("bram", state.stage());
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("approach") && ((Effect.NpcCommand) e).character().equals("bram")));

        // Bram arrives from his patrol; then the compass cinematic, the last lines and the outcome.
        assertEquals(Optional.of("bram_report"), approach("bram", 3));
        assertTrue(logged(Effect.GiveItem.class, e -> "Strange Compass".equals(((Effect.GiveItem) e).name())));
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s002_compass")));
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s002_ruin_light_on")));
        assertTrue(logged(Effect.WorldChange.class, e -> ((Effect.WorldChange) e).change().equals("mc001_s002_ruin_light_off")));

        assertTrue(state.isSceneComplete("mc001_s002"));
        assertNull(state.activeScene());
        assertTrue(state.hasFlag("mc001.s002.complete"));
        assertTrue(state.hasFlag("mc001.s003.survey_approved"));
        assertTrue(state.journal().contains("northern_disturbance"));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).subtitle().equals("The Northern Disturbance")));

        // The screenplay's spoken lines, verbatim and in order (clue remarks in between are gameplay text).
        List<String> screenplay = List.of(
                "Elder Rowan: Steve, I've been looking everywhere for you.",
                "Steve: What's wrong?",
                "Elder Rowan: Something happened near the old northern ruins before sunrise.",
                "Elara: The ruins? Nobody's been working there.",
                "Elder Rowan: Exactly. The ground shook, and the watchtower reported strange lights beneath the stone.",
                "Steve: An earthquake?",
                "Elder Rowan: I lived through earthquakes when I was younger. This felt different.",
                "Elara: Different how?",
                "Elder Rowan: Almost deliberate.",
                "Bram: The northern path isn't safe. Loose rocks came down just before dawn.",
                "Steve: Anyone hurt?",
                "Bram: No. I closed the trail before the traders reached it.",
                "Elder Rowan: Good. I'd rather lose a morning of trading than a single life.",
                "Steve: I'll have a look.",
                "Elder Rowan: Not alone. We need to know what we're dealing with.",
                "Elara: That's where I was heading.",
                "Steve: Then let's find out what happened.",
                "Elder Rowan: First, prepare properly. There'll be no heroics today.");
        List<String> said = spoken.stream().filter(screenplay::contains).toList();
        assertEquals(screenplay, said);
        assertEquals(screenplay.size() + 6, spoken.size(), "two remarks for each of the three clues");
    }

    @Test
    void cluesHaveBlocksInsideFirstbrook() {
        assertEquals(3, scene.clues().size());
        for (SceneDef.ClueDef c : scene.clues().values()) {
            assertNotNull(c.dialogue(), c.id());
            assertNotNull(content.dialogue(c.dialogue()), c.id());
            assertTrue(Math.abs(c.pos()[0]) < 44 && Math.abs(c.pos()[2]) < 44, c.id() + " is inside the village");
        }
    }

    @Test
    void loggingOutBeforeRowanSpeaksReplaysTheWarning() {
        run(engine.startScene(state, "mc001_s002"));
        state.markTriggerFired("rowan_warning");
        state = PlayerStoryState.fromJson(state.toJson());
        engine.resume(state);
        assertFalse(state.triggerFired("rowan_warning"));
        assertEquals(Optional.of("rowan_warning"), approach("elder_rowan", 3));
    }
}
