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

/** Plays MC-001 Scene 014, "The Records of Sunspire": the stacks, the glyphs, the great map and the crate. */
class Scene014PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s014");

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

    boolean gave(String item, String name) {
        return logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals(item)
                && (name == null || name.equals(((Effect.GiveItem) e).name())));
    }

    void examine(String clue) {
        run(engine.handle(state, new StoryEvent.ClueExamined(clue)));
    }

    @Test
    void fullPlaythrough() {
        assertEquals("sunspire", scene.site());
        run(engine.startScene(state, "mc001_s014"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("gate_road")));
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s014_arrival")));
        assertEquals("to_records", state.stage());

        walkInto("market"); // the area hint, and no progress
        assertEquals("to_records", state.stage());
        walkInto("sahir_meet");
        assertEquals("catalogue", state.stage());

        examine("s14_glyph_open"); // not yet
        for (String c : List.of("s14_cat_1", "s14_cat_2", "s14_cat_3")) examine(c);
        assertTrue(gave("minecraft:paper", "Catalogue notes"));
        assertEquals("glyphs", state.stage());

        // A wrong glyph clears the wall and runs the stage's on_wrong hint.
        examine("s14_glyph_open");
        examine("s14_glyph_hour");
        assertEquals("glyphs", state.stage());
        assertTrue(state.cluesFound().stream().noneMatch(c -> c.startsWith("s14_glyph")));
        assertTrue(logged(Effect.Hint.class, e -> ((Effect.Hint) e).text().startsWith("The glyphs sink back")));
        for (String g : List.of("s14_glyph_open", "s14_glyph_circle", "s14_glyph_only", "s14_glyph_hour")) examine(g);
        assertTrue(worldChange("mc001_s014_glyphs"));
        assertEquals("warning", state.stage());

        examine("s14_warning");
        assertEquals("manuscript", state.stage());
        examine("s14_manuscript");
        assertEquals("map", state.stage());
        assertTrue(gave("minecraft:torch", null));
        assertEquals(3, SceneEngine.stageBlocks(scene.stage("map")).spots().size());
        run(engine.handle(state, new StoryEvent.BlocksDone("map")));
        assertTrue(logged(Effect.Particles.class, e -> ((Effect.Particles) e).location().equals("valley_mark")));
        assertTrue(worldChange("mc001_s014_map_lines"));
        assertEquals("crate", state.stage());

        examine("s14_crate");
        assertTrue(worldChange("mc001_s014_crate_open"));
        assertTrue(state.isSceneComplete("mc001_s014"));
        assertTrue(gave("minecraft:chiseled_sandstone", "Solkar fragment"));
        assertTrue(state.hasFlag("mc001.solkar.joined"));
        assertTrue(state.hasFlag("mc001.s014.complete"));
        assertTrue(state.journal().contains("s14_sunspire_records"));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).title().equals("Mission Complete")
                && ((Effect.ShowTitle) e).subtitle().equals("The Sunspire Records")));

        assertEquals(List.of(
                "Elara: It's so bright.",
                "Orin: That tower's been here longer than the city around it, if the stories are right.",
                "Steve: Let's find Sahir.",
                "Sahir: I recognised the symbol as soon as the Queen's messenger described it.",
                "Steve: You've seen it before?",
                "Sahir: Not with my own eyes. But I know where it's recorded.",
                "Sahir: A survey conducted many generations ago mentioned a circle beneath six landmarks.",
                "Orin: Six landmarks?",
                "Sahir: Each corresponding to one of the great civilisations.",
                "Steve: Why did nobody know about this before?",
                "Sahir: Because the records were incomplete. Some pages were lost, others misunderstood.",
                "Elara: Could somebody have hidden them?",
                "Sahir: Possibly. But missing information isn't proof of a conspiracy.",
                "Orin: I'm starting to like him.",
                "Sahir: Thank you. I think.",
                "Sahir: 'Open the circle only in its hour. What is opened out of its hour will remember the hour it should have kept.'",
                "Orin: Remember? Stone doesn't remember anything.",
                "Sahir: The people who wrote this seem to have thought otherwise.",
                "Sahir: This one is worse. 'The deep note holds the world in time. Strike it wrongly, and time will keep two answers.'",
                "Elara: Two answers to what?",
                "Sahir: I wish I knew. The rest of the page is gone.",
                "Steve: The deep note... like the humming Liora's rangers heard?",
                "Sahir: Perhaps.",
                "Sahir: There. That symbol represents a central structure.",
                "Steve: The northern monument?",
                "Sahir: Perhaps. But it appears that the monument is only one part of something much larger.",
                "Steve: Everything leads back there.",
                "Sahir: Then whatever was hidden beneath the valley was important enough for six civilisations to record.",
                "Sahir: The crate your caravan saved was bound for this house. Solkar's piece.",
                "Steve: The raiders were after it.",
                "Sahir: My family's oldest book says it was given to us to keep, not to understand. Given by the First Hands, whoever they were.",
                "Sahir: Lord Kaelen of Emberhold has refused every letter about this. If Veyrath holds a piece, he won't give it up easily.",
                "Steve: Then we'll ask him in person."), spoken);
    }

    @Test
    void everyStageHasACheckpointAndCluesAreApart() {
        for (SceneDef.StageDef st : scene.stages()) assertNotNull(st.checkpoint(), st.id());
        for (SceneDef.StageDef st : scene.stages()) {
            if (st.completion() != SceneDef.CompletionType.CLUES && st.completion() != SceneDef.CompletionType.SEQUENCE) continue;
            List<String> ids = SceneEngine.stageClues(st);
            for (int i = 0; i < ids.size(); i++)
                for (int j = i + 1; j < ids.size(); j++) {
                    double[] a = scene.clues().get(ids.get(i)).pos(), b = scene.clues().get(ids.get(j)).pos();
                    double d = Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2));
                    assertTrue(d >= 2, st.id() + ": " + ids.get(i) + " and " + ids.get(j) + " are too close");
                }
        }
    }

    @Test
    void optionalCityLinesAreAmbient() {
        for (SceneDef.TriggerDef t : scene.triggers()) {
            assertTrue(t.ambient(), t.id());
            assertFalse(content.dialogue(t.dialogue()).critical(), t.id());
        }
    }
}
