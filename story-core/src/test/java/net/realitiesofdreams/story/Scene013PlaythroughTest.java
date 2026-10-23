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

/** Plays MC-001 Scene 013, "The Stone of Irondeep": the Forge Hall, the reconstruction and the Old Deeps. */
class Scene013PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s013");

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

    boolean given(String name) {
        return logged(Effect.GiveItem.class, e -> name.equals(((Effect.GiveItem) e).name()));
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
        assertEquals("irondeep", scene.site());
        run(engine.startScene(state, "mc001_s013"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("lift_car")));
        assertEquals("to_forge", state.stage());
        walkInto("forge_hall");
        assertEquals("fragment", state.stage());
        examine("s13_fragment");
        assertEquals("reconstruct", state.stage());
        assertTrue(given("Eldervale fragment") && given("Sylvaris fragment") && given("Stonecrown fragment"));
        assertEquals(3, SceneEngine.stageBlocks(scene.stage("reconstruct")).spots().size());
        build("reconstruct");
        assertEquals("to_deeps", state.stage());
        walkInto("deeps_landing");
        assertTrue(given("Dressed deepslate"));
        build("patch");
        assertTrue(worldChange("mc001_s013_drain"), "the hall was not drained");
        assertEquals("counterweights", state.stage());
        // A wrong order lets the weights drop back and resets the sequence.
        examine("s13_weight_large");
        examine("s13_weight_small");
        assertEquals("counterweights", state.stage());
        assertFalse(state.cluesFound().contains("s13_weight_large"));
        for (String w : List.of("s13_weight_large", "s13_weight_middle", "s13_weight_small")) examine(w);
        assertTrue(worldChange("mc001_s013_works_open"), "the works door did not open");
        assertEquals("evidence", state.stage());
        examine("s13_plate");
        examine("s13_gear_housing");
        assertTrue(state.isSceneComplete("mc001_s013"));
        assertTrue(state.hasFlag("mc001.stonecrown.joined"));
        assertTrue(state.hasFlag("mc001.s013.complete"));
        assertTrue(state.journal().contains("engraved_stone"));
        assertTrue(logged(Effect.GiveItem.class, e -> {
            Effect.GiveItem g = (Effect.GiveItem) e;
            return g.item().equals("minecraft:chiseled_deepslate") && g.lore().contains("The Builder machine plate from beneath Irondeep.");
        }));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).subtitle().equals("The Engraved Stone")));
        assertEquals(List.of(
                "Stonecrown Guard: A writ from Eldervale? The Thane's expecting you. Forge Hall, past the market terraces.",
                "Orin: Took you long enough. I came by the mountain road with Firstbrook's records.",
                "Steve: Orin! You came all this way?",
                "Orin: Somebody has to stop you touching the wrong thing.",
                "Thane Garrick: I was told you found something worth dragging me away from my forge.",
                "Steve: Queen Aveline sent us. She says your miners found something.",
                "Thane Garrick: My miners discovered this beneath the oldest foundations of Irondeep.",
                "Orin: That carving...", "Steve: It's the same symbol.",
                "Thane Garrick: Exactly. Which is why I want to know how you found another.",
                "Thane Garrick: Our deepest halls are older than the current kingdom. There are tunnels nobody remembers building.",
                "Steve: Could they connect to the northern monument?",
                "Thane Garrick: Not through any passage my miners know.",
                "Orin: The markings are consistent, though. Same spacing. Same geometry.",
                "Thane Garrick: Then somebody put considerable effort into making them identical.",
                "Orin: This isn't decoration. Look at the teeth on that edge. It's part of a machine.",
                "Thane Garrick: Stonecrown makes the finest gears in the six kingdoms. We never made this.",
                "Thane Garrick: That pattern resembles something I once saw in our oldest mine.",
                "Steve: What was it?",
                "Thane Garrick: A chamber sealed long before my grandfather's time.",
                "Elara: Can you open it?",
                "Thane Garrick: Perhaps. But I'd rather discover why it was sealed first.",
                "Thane Garrick: The Old Deeps flooded generations ago when the drains failed. If you want that chamber, we drain it first.",
                "Steve: Then we drain it.",
                "Orin: These machines weren't built by Stonecrown.",
                "Thane Garrick: No. My ancestors found them and swore to guard them. We built Irondeep around something older.",
                "Steve: The same builders who left the monument.",
                "Thane Garrick: Then Stonecrown stands with you. My engineers, my miners, whatever you need.",
                "Thane Garrick: Solkar's scholars have asked about that symbol for years. Sahir of Sunspire would trade his library for a look at this plate."),
                spoken);
    }

    @Test
    void everyStageHasACheckpoint() {
        for (SceneDef.StageDef s : scene.stages()) assertNotNull(s.checkpoint(), s.id());
    }

    @Test
    void sequenceCluesAreApartAndNamedBySize() {
        var clues = scene.clues();
        assertTrue(clues.get("s13_weight_large").name().contains("Largest"));
        assertTrue(clues.get("s13_weight_middle").name().contains("Middle"));
        assertTrue(clues.get("s13_weight_small").name().contains("Smallest"));
        List<String> ids = List.of("s13_weight_large", "s13_weight_middle", "s13_weight_small", "s13_plate", "s13_gear_housing");
        for (String a : ids)
            for (String b : ids) {
                if (a.equals(b)) continue;
                double[] p = clues.get(a).pos(), q = clues.get(b).pos();
                assertTrue(Math.abs(p[0] - q[0]) + Math.abs(p[2] - q[2]) >= 2, a + " and " + b + " are too close");
            }
    }
}
