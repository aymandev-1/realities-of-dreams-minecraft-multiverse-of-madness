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

/** Plays MC-001 Scene 001, "A Morning in Firstbrook", from the opening to completion the way the game drives it. */
class Scene001PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s001");

    PlayerStoryState state = new PlayerStoryState();
    final List<Effect> log = new ArrayList<>();
    final List<String> spoken = new ArrayList<>();

    /** Performs effects like the mod does: dialogues play to the end, cinematics finish (running their cues). */
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

    void walkInto(String location) {
        run(engine.checkAreas(state, l -> l == scene.location(location)));
        run(engine.handle(state, new StoryEvent.Reached(location)));
    }

    Effect.ShowObjective lastObjective() {
        for (int i = log.size() - 1; i >= 0; i--) if (log.get(i) instanceof Effect.ShowObjective o) return o;
        return null;
    }

    boolean logged(Class<?> type, java.util.function.Predicate<Object> test) {
        return log.stream().anyMatch(e -> type.isInstance(e) && test.test(e));
    }

    @Test
    void fullPlaythrough() {
        run(engine.startScene(state, "mc001_s001"));
        // Opening: Steve wakes in his house; the player takes over from the actor where he stands.
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s001_opening")));
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("sleep")));
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("despawn") && ((Effect.NpcCommand) e).character().equals("primordial_steve")));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("actor_stand")));
        assertEquals("leave_house", state.stage());
        assertEquals("Leave Steve's house", lastObjective().text());
        assertTrue(logged(Effect.Hint.class, e -> ((Effect.Hint) e).text().contains("{forward}")));

        // Walking past the storage chest shows the inventory hint once.
        run(engine.checkAreas(state, l -> l == scene.location("house_storage")));
        long hints = log.stream().filter(e -> e instanceof Effect.Hint).count();
        run(engine.checkAreas(state, l -> l == scene.location("house_storage")));
        assertEquals(hints, log.stream().filter(e -> e instanceof Effect.Hint).count());

        // Nobody has to be clicked: stepping outside, Elara falls in beside Steve and the walk starts.
        walkInto("outside_door");
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("follow") && ((Effect.NpcCommand) e).character().equals("elara")));
        assertEquals("explore", state.stage());
        assertTrue(state.hasFlag("mc001.s001.walk1"));

        // The farmer calls out as they pass; that finishes "Explore Firstbrook".
        assertEquals(Optional.of("farmer_morning"), approach("firstbrook_farmer_1", 7));
        assertEquals("to_square", state.stage());
        assertTrue(approach("firstbrook_farmer_1", 3).isEmpty(), "the greeting plays once");

        // Saving and loading mid-scene keeps everything.
        state = PlayerStoryState.fromJson(state.toJson());
        assertEquals("to_square", state.stage());
        assertTrue(state.hasFlag(SceneEngine.FOLLOW_FLAG + "elara"));

        walkInto("square");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s001_ridge")));
        assertTrue(logged(Effect.Shake.class, e -> true));
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("look") && ((Effect.NpcCommand) e).character().equals("elara")));

        // Rowan calls from across the square and comes over; the mission completes.
        assertTrue(logged(Effect.Say.class, e -> ((Effect.Say) e).character().equals("elder_rowan") && ((Effect.Say) e).text().equals("Steve! There you are.")));
        assertTrue(logged(Effect.NpcCommand.class, e -> ((Effect.NpcCommand) e).kind().equals("approach") && ((Effect.NpcCommand) e).character().equals("elder_rowan")));
        assertTrue(state.isSceneComplete("mc001_s001"));
        assertTrue(state.hasFlag("mc001.s001.complete"));
        assertTrue(state.hasFlag("mc001.firstbrook.explore_unlocked"));
        assertTrue(state.hasValue("rel.elara"), "Elara's companion relationship is initialised");
        assertTrue(state.journal().contains("firstbrook_morning"));
        assertEquals(5, log.stream().filter(e -> e instanceof Effect.GiveItem).count());
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).title().equals("Mission Complete")));
        assertTrue(logged(Effect.SceneCompleted.class, e -> true));

        // Every line of the screenplay's walking conversation and foreshadowing, in order and verbatim.
        assertEquals(List.of(
                "Elara: You repaired the eastern bridge yesterday?",
                "Steve: Most of it. Bram helped with the supports.",
                "Elara: You mean Bram watched while you did everything?",
                "Steve: He carried the wood.",
                "Elara: That's something, I suppose.",
                "Farmer: Morning, Steve!",
                "Steve: Morning!",
                "Elara: I wish every morning was this quiet.",
                "Steve: Don't say that. Something always happens when somebody says that.",
                "Steve: Did you feel that?",
                "Elara: Feel what?",
                "Steve: Probably nothing.",
                "Elara: I hope so."), spoken);
    }

    @Test
    void castMatchesTheScreenplay() {
        // The sleeping Steve is only an actor for the opening; Rowan only appears for the ending.
        SceneDef.NpcPlacement actor = scene.placementsFor("primordial_steve").get(0);
        assertTrue(scene.presentDuring(actor, "opening"));
        assertFalse(scene.presentDuring(actor, "leave_house"));
        SceneDef.NpcPlacement rowan = scene.placementsFor("elder_rowan").get(0);
        assertFalse(scene.presentDuring(rowan, "feel_that"));
        assertTrue(scene.presentDuring(rowan, "rowan"));
        for (String c : List.of("elara", "orin", "bram", "firstbrook_farmer_1", "firstbrook_guard_1", "firstbrook_villager_1")) {
            assertFalse(scene.placementsFor(c).isEmpty(), c + " is placed in Firstbrook");
        }
        assertEquals("patrol", scene.placementsFor("firstbrook_guard_1").get(0).activity());
        assertEquals("work", scene.placementsFor("firstbrook_farmer_1").get(0).activity());
        assertEquals("firstbrook", scene.site());
    }

    @Test
    void farmerWaitsForTheWalkingConversation() {
        run(engine.startScene(state, "mc001_s001"));
        state.clearFlag("mc001.s001.walk1");
        state.setStage("explore");
        assertTrue(approach("firstbrook_farmer_1", 5).isEmpty());
    }

    @Test
    void loggingOutMidConversationCannotStickTheScene() {
        run(engine.startScene(state, "mc001_s001"));
        // Logged out halfway through the walking conversation: it plays again on return.
        state.setStage("explore");
        state = PlayerStoryState.fromJson(state.toJson());
        assertTrue(engine.resume(state).stream().anyMatch(e -> e instanceof Effect.StartDialogue d && d.dialogueId().equals("walk_bridge")));
        // Once it has finished it is not repeated.
        run(engine.handle(state, new StoryEvent.DialogueFinished("walk_bridge")));
        state.setFlag("mc001.s001.walk1");
        assertTrue(engine.resume(state).stream().noneMatch(e -> e instanceof Effect.StartDialogue));
        // Cut off mid-greeting by the farmer: he greets Steve again next time.
        state.markTriggerFired("farmer_morning");
        engine.resume(state);
        assertFalse(state.triggerFired("farmer_morning"));
        // Logged out during "Did you feel that?": it replays, since no character trigger starts it.
        state.setStage("feel_that");
        assertTrue(engine.resume(state).stream().anyMatch(e -> e instanceof Effect.StartDialogue d && d.dialogueId().equals("feel_that")));
    }
}
