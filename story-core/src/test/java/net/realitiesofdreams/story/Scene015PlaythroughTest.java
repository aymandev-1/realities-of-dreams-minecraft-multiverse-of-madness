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

/**
 * Plays MC-001 Scene 015, "The Lord of Emberhold": the arrival, the council and Kaelen's question,
 * the evidence, the trial of the vault and the Ember Key.
 */
class Scene015PlaythroughTest {
    final StoryContent content = StoryContent.loadFromClasspath(getClass());
    final SceneEngine engine = new SceneEngine(content);
    final SceneDef scene = content.scene("mc001_s015");

    PlayerStoryState state = new PlayerStoryState();
    final List<Effect> log = new ArrayList<>();
    final List<String> spoken = new ArrayList<>();
    int choice;

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
                    queue.addAll(engine.applyActions(state, s.awaitingChoice() ? s.choose(choice) : s.advance()));
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

    void examine(String clue) {
        run(engine.handle(state, new StoryEvent.ClueExamined(clue)));
    }

    /** Plays up to the end of the council, answering Kaelen with the given choice. */
    void playToEvidence(int answer) {
        choice = answer;
        run(engine.startScene(state, "mc001_s015"));
        assertTrue(logged(Effect.TeleportPlayer.class, e -> ((Effect.TeleportPlayer) e).location().equals("road_west")));
        assertEquals("causeway", state.stage());
        walkInto("causeway_mid");
        assertEquals("to_hall", state.stage());
        walkInto("hall_inside");
        assertEquals("examine", state.stage());
    }

    @Test
    void fullPlaythrough() {
        assertEquals("emberhold", scene.site());
        assertEquals("mc001_s016", scene.nextScene());
        playToEvidence(0);
        assertTrue(state.hasFlag("mc001.kaelen.approach_evidence"));
        assertEquals(1, state.value("mc001.kaelen.trust"));

        // Examine the three pieces, in any order. Presenting is not possible yet.
        examine("s15_present_grove");
        assertEquals("examine", state.stage());
        for (String c : List.of("s15_ev_map", "s15_ev_grove", "s15_ev_fragments")) examine(c);
        assertEquals("present", state.stage());

        // A wrong order costs Kaelen's trust and starts the presentation again.
        examine("s15_present_grove");
        examine("s15_present_map");
        assertEquals("present", state.stage());
        assertEquals(0, state.value("mc001.kaelen.trust"));
        for (String c : List.of("s15_present_grove", "s15_present_fragments", "s15_present_map")) examine(c);
        assertEquals("dam", state.stage());
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:polished_blackstone_bricks")
                && ((Effect.GiveItem) e).count() >= 18));
        assertEquals(18, SceneEngine.stageBlocks(scene.stage("dam")).spots().size());
        run(engine.handle(state, new StoryEvent.BlocksDone("dam")));
        assertEquals("braziers", state.stage());

        // The braziers in the oath's order: a wrong one gutters out and resets.
        examine("s15_brazier_apart");
        assertEquals("braziers", state.stage());
        assertTrue(logged(Effect.PlaySound.class, e -> ((Effect.PlaySound) e).sound().equals("minecraft:block.fire.extinguish")));
        for (String c : List.of("s15_brazier_keep", "s15_brazier_apart", "s15_brazier_never")) examine(c);
        assertTrue(worldChange("mc001_s015_vault_open"), "the vault did not open");
        assertTrue(worldChange("mc001_s015_braziers_lit"));
        assertEquals("key", state.stage());
        examine("s15_ember_key");

        assertTrue(state.isSceneComplete("mc001_s015"));
        assertTrue(state.hasFlag("mc001.veyrath.joined"));
        assertTrue(state.hasFlag("mc001.s015.complete"));
        assertTrue(state.journal().contains("s15_journal"));
        assertTrue(logged(Effect.GiveItem.class, e -> ((Effect.GiveItem) e).item().equals("minecraft:chiseled_nether_bricks")
                && "Veyrath fragment".equals(((Effect.GiveItem) e).name())));
        assertTrue(logged(Effect.ShowTitle.class, e -> ((Effect.ShowTitle) e).subtitle().equals("A Divided Council")));

        assertEquals(List.of(
                "Bram: Is it always this warm?", "Elara: Don't touch the walls.",
                "Lord Kaelen: So this is the discovery everyone has been whispering about.",
                "Queen Aveline: You were invited to examine the evidence.",
                "Lord Kaelen: I was invited after the others had already begun their investigation.",
                "Thane Garrick: We barely understand what we found.",
                "Lord Kaelen: And yet you've already carried pieces of it into another kingdom.",
                "Lord Kaelen: I will not allow an ancient Veyrath legacy to be claimed by whoever reached it first.",
                "Steve: Nobody's claiming anything.", "Lord Kaelen: Not yet.",
                "Liora: The evidence was discovered across several territories. This concerns us all.",
                "Lord Kaelen: That is easy to say when your people already hold part of it.",
                "Queen Aveline: Enough. We will establish a joint expedition and keep an open record of every discovery.",
                "Lord Kaelen: Then Veyrath expects equal representation.", "Steve: That's fair.",
                "Lord Kaelen: And who gave you authority to lead this?",
                "Steve: Nobody. I was looking into something dangerous near my home.",
                "Steve: If you know more about it, help us.",
                "Lord Kaelen: Then tell me why Veyrath should open its vault to strangers.",
                "Steve: Because the records show all six kingdoms were given a piece to keep. Yours too.",
                "Lord Kaelen: Records. Scholars' ink. Show me, then.",
                "Lord Kaelen: Six kingdoms, one pattern. Very well. You've earned the truth.",
                "Lord Kaelen: My house swore an oath before Emberhold had walls. Keep the Ember Key apart from the door, and never carry it in anger.",
                "Steve: Door? What door?",
                "Lord Kaelen: The oath never said. Only that the key and the door must not meet by chance.",
                "Elara: You've guarded it all this time without knowing why?",
                "Lord Kaelen: Veyrath keeps its word. That is the why.",
                "Lord Kaelen: Every kingdom calls Veyrath the dangerous one. Fire, forges, temper. Yet we kept the most dangerous thing of all, and kept it safe.",
                "Lord Kaelen: Very well. Veyrath will send its experts.",
                "Queen Aveline: Then we have an agreement.",
                "Lord Kaelen: An agreement to investigate. Not to surrender our interests.",
                "Steve: That's a start.",
                "Lord Kaelen: And the key does not leave my sight. Where it goes, Veyrath goes.",
                "Queen Aveline: Captain Maren sends word from Tidewatch. Her divers have found something at the old coastal watch.",
                "Steve: The last piece."), spoken);
    }

    @Test
    void eachAnswerToKaelenHasItsOwnReply() {
        playToEvidence(1);
        assertTrue(state.hasFlag("mc001.kaelen.approach_respect"));
        assertEquals(1, state.value("mc001.kaelen.trust"));
        assertTrue(spoken.contains("Lord Kaelen: Flattery from Firstbrook. Still, it happens to be true."));

        state = new PlayerStoryState();
        spoken.clear();
        playToEvidence(2);
        assertTrue(state.hasFlag("mc001.kaelen.approach_warning"));
        assertFalse(state.hasFlag("mc001.kaelen.approach_evidence"));
        assertEquals(0, state.value("mc001.kaelen.trust"));
        assertTrue(spoken.contains("Lord Kaelen: You think I haven't lain awake over that? Prove you understand it better than I do."));
    }

    @Test
    void sequenceCluesShareTheExaminedCluesPlaces() {
        for (String k : List.of("grove", "fragments", "map")) {
            SceneDef.ClueDef ev = scene.clues().get("s15_ev_" + k), pr = scene.clues().get("s15_present_" + k);
            assertArrayEquals(ev.pos(), pr.pos(), k);
            assertEquals(ev.block(), pr.block(), k);
        }
        for (SceneDef.StageDef st : scene.stages()) assertNotNull(st.checkpoint(), "no checkpoint on " + st.id());
    }
}
