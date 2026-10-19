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

/** Plays MC-001 Scene 005, "Bram's Volunteers": the road is secured by real building. */
class Scene005PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s005");

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

    @Test
    void theWorkIsRealBlocks() {
        SceneEngine.BlockGoal barricade = SceneEngine.stageBlocks(scene.stage("barricade"));
        assertEquals("solid", barricade.mode());
        assertEquals(6, barricade.spots().size());
        assertArrayEquals(new int[] {4, 0, -32}, barricade.spots().get(0));
        assertEquals("light", SceneEngine.stageBlocks(scene.stage("lights")).mode());
        assertEquals("minecraft:barrel", SceneEngine.stageBlocks(scene.stage("supplies")).mode());
        assertEquals("air", SceneEngine.stageBlocks(scene.stage("hazard")).mode());
        assertEquals(10, SceneEngine.stageBlocks(scene.stage("hazard")).spots().size());
    }

    @Test
    void fullPlaythrough() {
        assertEquals("mc001_s005", content.scene("mc001_s004").nextScene());
        run(engine.startScene(state, "mc001_s005"));
        assertTrue(worldChange("mc001_s005_worksite"), "the work site was not laid out");
        walkInto("gate_outside");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s005_volunteers")));
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:oak_planks") && ((Effect.GiveItem) e).count() == 12));

        // Progress is whatever the game last counted; another stage's event cannot finish the barricade.
        state.setValue(SceneEngine.BLOCKS_PROGRESS, 4);
        assertEquals("4/6", ((Effect.ShowObjective) engine.objective(state, scene, scene.stage("barricade"))).progress());
        run(engine.handle(state, new StoryEvent.BlocksDone("platform")));
        assertEquals("barricade", state.stage());

        build("barricade");
        build("platform");
        build("lights");
        build("supplies");
        assertEquals("hazard", state.stage());
        assertTrue(worldChange("mc001_s005_rockfall"), "the rockfall did not come down");
        build("hazard");
        walkInto("platform_top");
        assertTrue(worldChange("mc001_s005_monument"), "the monument was not raised on the horizon");
        assertTrue(logged(Effect.StartCinematic.class, e -> ((Effect.StartCinematic) e).cinematicId().equals("mc001_s005_rise")));
        assertTrue(state.isSceneComplete("mc001_s005"));
        assertTrue(state.hasFlag("mc001.north_road.secure") && state.hasFlag("mc001.act1.complete"));
        assertTrue(state.journal().contains("north_road"));

        assertEquals(List.of(
                "Bram: Everyone listen! Nobody goes past the north marker without an escort.", "Villager: It's just an old ruin.",
                "Bram: Then you won't mind waiting until we know it's safe.", "Bram: Good. I need somebody who knows how to build a proper barricade.",
                "Steve: You could've just asked.", "Bram: I was hoping you'd volunteer.",
                "Worker: Help! I can't get through!", "Steve: Stay back from the rocks!", "Bram: I'll clear the other side!",
                "Bram: Nicely done.", "Steve: We're lucky nobody got hurt.", "Bram: Luck won't keep us safe forever.",
                "Elara: There it is.", "Steve: The ruins?", "Orin: Not exactly. That structure doesn't match any record I've seen.",
                "Elder Rowan: Then we proceed carefully.", "Steve: We go tomorrow."), spoken);
    }
}
