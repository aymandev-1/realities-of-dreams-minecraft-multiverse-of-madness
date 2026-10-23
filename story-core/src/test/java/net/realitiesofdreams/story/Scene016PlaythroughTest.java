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

/** Plays MC-001 Scene 016, "The Fragment from Tidewatch": the harbour work, the dive and the six fragments. */
class Scene016PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s016");

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

    boolean given(String item, String name) {
        return logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals(item) && name.equals(((Effect.GiveItem) e).name()));
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
        assertEquals("tidewatch", scene.site());
        run(engine.startScene(state, "mc001_s016"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("headland_road")));
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s016_arrival")));
        assertEquals("to_docks", state.stage());

        walkInto("maren_meet");
        assertEquals("cargo", state.stage());
        examine("s16_barrel_1");
        examine("s16_barrel_2");
        examine("s16_housing"); // not this stage's
        assertEquals("cargo", state.stage());
        examine("s16_barrel_3");

        assertEquals("crane", state.stage());
        assertTrue(given("minecraft:spruce_log", "Crane beam timber"));
        assertTrue(given("minecraft:chain", "Crane chain"));
        build("crane");
        assertTrue(worldChange("mc001_s016_crane_fixed"));

        assertEquals("swim", state.stage());
        assertTrue(given("minecraft:turtle_helmet", "Tidewatch diving helmet"));
        assertEquals("pier", scene.stage("swim").checkpoint(), "drowning on the dive must bring the player back to the pier");
        walkInto("watch_room");
        build("clear");
        assertEquals("take", state.stage());
        examine("s16_housing");
        assertTrue(worldChange("mc001_s016_anchor_taken"));
        assertEquals("swim_back", state.stage());
        for (String s : List.of("swim", "clear", "take", "swim_back", "anchor_talk")) assertEquals("pier", scene.stage(s).checkpoint(), s);

        walkInto("pier_end");
        assertEquals("lay", state.stage());
        assertTrue(worldChange("mc001_s016_fragments"));
        assertTrue(given("minecraft:dark_prismarine", "Tide Anchor"));
        build("lay");

        assertTrue(logged(Effect.Particles.class, e -> ((Effect.Particles) e).location().equals("light_7")));
        assertTrue(state.isSceneComplete("mc001_s016"));
        assertTrue(state.hasFlag("mc001.thalassia.joined"));
        assertTrue(state.hasFlag("mc001.s016.complete"));
        assertTrue(state.journal().contains("s16_sixth_fragment"));
        assertTrue(given("minecraft:dark_prismarine", "Thalassia fragment"));
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).lore().equals(List.of("The Tide Anchor of Tidewatch. Made to hold something steady."))));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).title().equals("Mission Complete")
                && ((Effect.ShowTitle) e).subtitle().equals("The Sixth Fragment")));
        for (SceneDef.StageDef s : scene.stages()) assertNotNull(s.checkpoint(), "stage " + s.id() + " has no checkpoint");

        assertEquals(List.of(
                "Captain Maren: I'd have met you on the road, but the sea wasn't cooperative.",
                "Orin: Have you got the fragment?",
                "Captain Maren: Found it. Then the tremors brought half the old watch down on top of it.",
                "Captain Maren: We found it beneath an old coastal watch structure.",
                "Sahir: Then the same builders may have worked across all six territories.",
                "Captain Maren: I heard you helped a caravan outside Eldervale.",
                "Steve: We were in the right place.",
                "Captain Maren: The right place is usually where someone chooses to stand.",
                "Steve: You sound like Rowan.",
                "Captain Maren: Then Rowan sounds sensible.",
                "Captain Maren: Follow the buoys out and keep to the bubble lines. Tidewatch divers have breathed off them for three hundred years.",
                "Captain Maren: The Tide Anchor. My grandfather swore it kept this coast from shaking.",
                "Orin: Maybe it did. Look how it's made. It's meant to hold something steady.",
                "Captain Maren: Well. That's new.",
                "Orin: I think we've just found our next destination.",
                "Captain Maren: I'll carry everyone to Valebridge myself. Every kingdom should be in one room for this."),
                spoken.stream().map(l -> l.replaceFirst("^Maren: ", "Captain Maren: ")).toList());
    }

    @Test
    void harbourChatterIsOptional() {
        for (SceneDef.TriggerDef t : scene.triggers()) {
            assertTrue(t.ambient(), t.id());
            assertFalse(content.dialogue(t.dialogue()).critical(), t.id());
        }
    }
}
