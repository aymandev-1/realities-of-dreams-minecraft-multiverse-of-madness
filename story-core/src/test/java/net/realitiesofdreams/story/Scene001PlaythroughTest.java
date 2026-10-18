package net.realitiesofdreams.story;

import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.CompanionChatter;
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
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Plays MC-001 Scene 001 from arrival to completion the way the game layer drives it. */
class Scene001PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s001");

    PlayerStoryState state = new PlayerStoryState();
    final List<Effect> log = new ArrayList<>();
    final List<String> spoken = new ArrayList<>();

    /** Performs effects like the mod does: dialogues play to the end (first choice), cinematics finish. */
    void run(List<Effect> effects) {
        Deque<Effect> queue = new ArrayDeque<>(effects);
        while (!queue.isEmpty()) {
            Effect e = queue.poll();
            log.add(e);
            if (e instanceof Effect.StartCinematic c) {
                queue.addAll(engine.handle(state, new StoryEvent.CinematicFinished(c.cinematicId())));
            } else if (e instanceof Effect.StartDialogue d) {
                DialogueSession s = new DialogueSession(content.dialogue(d.dialogueId()));
                queue.addAll(engine.applyActions(state, s.start()));
                while (!s.finished()) {
                    spoken.add(s.current().speaker() + ": " + s.current().text());
                    queue.addAll(engine.applyActions(state, s.awaitingChoice() ? s.choose(0) : s.advance()));
                }
                queue.addAll(engine.handle(state, new StoryEvent.DialogueFinished(d.dialogueId())));
            }
        }
    }

    /** The player walks close to a character; whoever the director picks starts talking. */
    Optional<String> approach(String character, double distance) {
        Optional<ProximityDirector.Decision> d = ProximityDirector.choose(scene, state,
                List.of(new NearbyCharacter(character, distance, true)));
        d.ifPresent(dec -> {
            state.markTriggerFired(dec.trigger().id());
            run(List.of(new Effect.StartDialogue(dec.trigger().dialogue())));
        });
        return d.map(dec -> dec.trigger().id());
    }

    Effect.ShowObjective lastObjective() {
        for (int i = log.size() - 1; i >= 0; i--) if (log.get(i) instanceof Effect.ShowObjective o) return o;
        return null;
    }

    @Test
    void fullPlaythrough() {
        run(engine.startScene(state, "mc001_s001"));
        assertTrue(log.stream().anyMatch(e -> e instanceof Effect.StartCinematic c && c.cinematicId().equals("mc001_s001_opening")));
        assertEquals("find_rowan", state.stage());
        assertEquals("Find Elder Rowan in the village square", lastObjective().text());

        // Elara is not ready to talk yet, and Bram greets the player on the way in.
        assertTrue(approach("elara", 4).isEmpty());
        assertEquals(Optional.of("bram_hello"), approach("bram", 5));
        assertTrue(approach("bram", 5).isEmpty(), "ambient greeting only plays once");

        // Rowan notices the player from across the square and walks over.
        ProximityDirector.Decision d = ProximityDirector.choose(scene, state, List.of(new NearbyCharacter("elder_rowan", 10, true))).orElseThrow();
        assertTrue(d.approach(), "Rowan should walk up rather than shout from 10 blocks");
        assertEquals(Optional.of("rowan_greeting"), approach("elder_rowan", 3));
        assertTrue(state.hasFlag("mc001.s001.met_rowan"));
        assertEquals("meet_elara", state.stage());

        assertEquals(Optional.of("elara_meet"), approach("elara", 6));
        assertTrue(state.hasFlag(SceneEngine.FOLLOW_FLAG + "elara"));
        assertEquals("to_shrine", state.stage());

        // Companion chatter while walking the trail, never repeating.
        Random rng = new Random(1);
        Set<String> said = new java.util.HashSet<>();
        Optional<SceneDef.ChatterDef> c;
        while ((c = CompanionChatter.pick(scene, state, "moving", Set.of("elara"), rng)).isPresent()) {
            assertTrue(said.add(c.get().id()));
        }
        assertEquals(3, said.size());

        // Saving mid-scene: serialise, reload, resume.
        state = PlayerStoryState.fromJson(state.toJson());
        List<Effect> resumed = engine.resume(state);
        assertTrue(resumed.stream().anyMatch(e -> e instanceof Effect.NpcCommand n && n.kind().equals("follow")));
        assertTrue(resumed.stream().anyMatch(e -> e instanceof Effect.ShowObjective o && o.text().contains("Hollow Shrine")));

        run(engine.handle(state, new StoryEvent.Reached("shrine")));
        assertTrue(log.stream().anyMatch(e -> e instanceof Effect.WorldChange w && w.change().equals("mc001_rift_scar")));
        assertEquals("examine", state.stage());
        assertEquals("0/3", lastObjective().progress());

        run(engine.handle(state, new StoryEvent.ClueExamined("rift_stone")));
        run(engine.handle(state, new StoryEvent.ClueExamined("rift_stone")));
        assertEquals("1/3", lastObjective().progress());
        run(engine.handle(state, new StoryEvent.ClueExamined("journal_page")));
        assertTrue(log.stream().anyMatch(e -> e instanceof Effect.GiveItem g && g.name().equals("Torn Journal Page")));
        run(engine.handle(state, new StoryEvent.ClueExamined("footprints")));
        assertTrue(state.hasFlag("mc001.s001.saw_shrine"), "conclusion dialogue played");
        assertEquals("report", state.stage());
        assertTrue(log.stream().anyMatch(e -> e instanceof Effect.NpcCommand n && n.kind().equals("spawn") && n.character().equals("captain_maren")));

        assertTrue(approach("liora", 3).isEmpty(), "Liora's rumour is stale after the shrine");
        assertEquals(Optional.of("rowan_report"), approach("elder_rowan", 8));

        assertNull(state.activeScene());
        assertTrue(state.isSceneComplete("mc001_s001"));
        assertTrue(state.hasFlag("mc001.s001.complete"));
        assertFalse(state.hasFlag(SceneEngine.FOLLOW_FLAG + "elara"));
        assertTrue(log.stream().anyMatch(e -> e instanceof Effect.SceneCompleted s && s.sceneId().equals("mc001_s001")));
        assertTrue(spoken.stream().anyMatch(s -> s.startsWith("captain_maren:")));
    }

    @Test
    void loggingOutDuringACinematicSkipsIt() {
        run(engine.startScene(state, "mc001_s001"));
        state.setStage("tremor");
        List<Effect> resumed = engine.resume(state);
        assertEquals("examine", state.stage());
        assertTrue(resumed.stream().anyMatch(e -> e instanceof Effect.ShowObjective));
    }

    @Test
    void wrongEventsDoNotAdvance() {
        run(engine.startScene(state, "mc001_s001"));
        run(engine.handle(state, new StoryEvent.Reached("shrine")));
        run(engine.handle(state, new StoryEvent.ClueExamined("rift_stone")));
        run(engine.handle(state, new StoryEvent.DialogueFinished("elara_meet")));
        assertEquals("find_rowan", state.stage());
        assertTrue(state.cluesFound().isEmpty());
    }
}
