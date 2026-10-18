package net.realitiesofdreams.story.scene;

import net.realitiesofdreams.story.StoryContent;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.dialogue.DialogueDef;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.universe.UniverseId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Cross-checks every reference in the story content so broken data is caught by tests, not players. */
public final class SceneValidator {
    private static final Set<String> ACTIONS = Set.of("set_flag", "clear_flag", "start_dialogue", "start_cinematic",
            "npc_follow", "npc_stay", "npc_move", "npc_spawn", "npc_despawn", "title", "give_item", "hold_item", "sound",
            "world_change", "message", "npc_sleep", "npc_wake", "npc_approach", "npc_look", "hint", "shake", "say",
            "teleport_player", "journal", "set_value", "add_value", "particles");

    private SceneValidator() {}

    public static List<String> validate(StoryContent content) {
        List<String> errors = new ArrayList<>();
        content.characters().all().forEach(c -> {
            if (c.universe() != null && !UniverseId.isValidReference(c.universe())) {
                errors.add("character " + c.id() + ": invalid universe " + c.universe());
            }
            if (c.usesPlayerModel() && c.texture() == null) errors.add("character " + c.id() + ": no texture");
        });
        for (SceneDef s : content.scenes()) {
            validateScene(content, s, errors);
            validateCast(content, s, errors);
        }
        content.dialogues().values().forEach(d -> validateDialogue(content, d, errors));
        return errors;
    }

    private static void validateScene(StoryContent content, SceneDef s, List<String> errors) {
        String p = "scene " + s.id() + ": ";
        if (!UniverseId.isValidReference(s.universe())) errors.add(p + "invalid universe " + s.universe());
        if (s.stages().isEmpty()) errors.add(p + "no stages");
        Set<String> stageIds = new HashSet<>();
        for (SceneDef.StageDef st : s.stages()) {
            if (!stageIds.add(st.id())) errors.add(p + "duplicate stage " + st.id());
        }
        for (SceneDef.NpcPlacement n : s.npcs()) {
            character(content, n.character(), p + "npc", errors);
            location(s, n.location(), p + "npc " + n.character(), errors);
            if (n.fromStage() != null && !stageIds.contains(n.fromStage())) errors.add(p + "npc " + n.character() + " from unknown stage " + n.fromStage());
            if (n.untilStage() != null && !stageIds.contains(n.untilStage())) errors.add(p + "npc " + n.character() + " until unknown stage " + n.untilStage());
        }
        for (SceneDef.StageDef st : s.stages()) {
            String sp = p + "stage " + st.id() + ": ";
            if (st.target() != null) location(s, st.target(), sp + "target", errors);
            if (st.checkpoint() != null) location(s, st.checkpoint(), sp + "checkpoint", errors);
            if (st.next() != null && !stageIds.contains(st.next())) errors.add(sp + "next stage " + st.next() + " missing");
            String v = st.completionValue();
            switch (st.completion()) {
                case CINEMATIC_END -> { if (content.cinematic(v) == null) errors.add(sp + "cinematic " + v + " missing"); }
                case DIALOGUE_END -> { if (content.dialogue(v) == null) errors.add(sp + "dialogue " + v + " missing"); }
                case REACH -> location(s, v, sp + "reach", errors);
                case CLUES -> SceneEngine.stageClues(st).forEach(c -> { if (!s.clues().containsKey(c)) errors.add(sp + "clue " + c + " missing"); });
                case FLAG -> { if (v == null) errors.add(sp + "flag completion without a flag"); }
                case ITEMS -> { if (v == null || SceneEngine.stageItems(st).isEmpty()) errors.add(sp + "items completion without items"); }
            }
            actions(content, s, st.onEnter(), sp, errors);
            actions(content, s, st.onComplete(), sp, errors);
        }
        for (SceneDef.TriggerDef t : s.triggers()) {
            character(content, t.character(), p + "trigger " + t.id(), errors);
            if (content.dialogue(t.dialogue()) == null) errors.add(p + "trigger " + t.id() + ": dialogue " + t.dialogue() + " missing");
            t.stages().forEach(st -> { if (!stageIds.contains(st)) errors.add(p + "trigger " + t.id() + ": unknown stage " + st); });
            if (t.talkRadius() > t.radius()) errors.add(p + "trigger " + t.id() + ": talk_radius larger than radius");
        }
        for (SceneDef.NpcPlacement n : s.npcs()) {
            n.route().forEach(r -> location(s, r, p + "npc " + n.character() + " route", errors));
            if (!Set.of("idle", "work", "patrol", "routine").contains(n.activity())) errors.add(p + "npc " + n.character() + ": unknown activity " + n.activity());
            if ((n.activity().equals("patrol") || n.activity().equals("routine")) && n.route().size() < 2)
                errors.add(p + "npc " + n.character() + ": a " + n.activity() + " needs two route points");
        }
        for (SceneDef.AreaTrigger t : s.areaTriggers()) {
            location(s, t.location(), p + "area trigger " + t.id(), errors);
            t.stages().forEach(st -> { if (!stageIds.contains(st)) errors.add(p + "area trigger " + t.id() + ": unknown stage " + st); });
            actions(content, s, t.actions(), p + "area trigger " + t.id() + ": ", errors);
        }
        for (String cid : content.cinematics().keySet()) {
            // Cinematic cues run in the scene that plays them; check their references against every scene that uses them.
            if (s.stages().stream().anyMatch(st -> st.completion() == SceneDef.CompletionType.CINEMATIC_END && cid.equals(st.completionValue()))) {
                for (var cue : content.cinematic(cid).cues()) actions(content, s, cue.actions(), p + "cinematic " + cid + " cue: ", errors);
            }
        }
        for (SceneDef.ChatterDef c : s.chatter()) {
            character(content, c.character(), p + "chatter " + c.id(), errors);
            if (c.context().startsWith("near:")) location(s, c.context().substring(5), p + "chatter " + c.id(), errors);
        }
        for (SceneDef.ClueDef c : s.clues().values()) {
            if (c.dialogue() != null && content.dialogue(c.dialogue()) == null) errors.add(p + "clue " + c.id() + ": dialogue missing");
        }
        if (s.nextScene() != null && content.scene(s.nextScene()) == null && !s.nextScene().startsWith("unreleased:")) {
            errors.add(p + "next scene " + s.nextScene() + " missing");
        }
    }

    /**
     * Every character a scene can put on screen or give a line must be cast in that scene's saga, so
     * later-saga characters (and alternate forms such as Primordial Steve's Empowered form) cannot
     * leak into an earlier story.
     */
    private static void validateCast(StoryContent content, SceneDef s, List<String> errors) {
        Set<String> used = new java.util.TreeSet<>();
        Set<String> dialogues = new HashSet<>();
        List<ActionDef> acts = new ArrayList<>();
        s.npcs().forEach(n -> used.add(n.character()));
        s.triggers().forEach(t -> { used.add(t.character()); dialogues.add(t.dialogue()); });
        s.chatter().forEach(c -> used.add(c.character()));
        s.clues().values().forEach(c -> { if (c.dialogue() != null) dialogues.add(c.dialogue()); });
        for (SceneDef.StageDef st : s.stages()) {
            acts.addAll(st.onEnter());
            acts.addAll(st.onComplete());
            if (st.completion() == SceneDef.CompletionType.DIALOGUE_END) dialogues.add(st.completionValue());
            if (st.completion() == SceneDef.CompletionType.CINEMATIC_END && content.cinematic(st.completionValue()) != null) {
                var cin = content.cinematic(st.completionValue());
                cin.cues().forEach(cue -> acts.addAll(cue.actions()));
                cin.subtitles().forEach(sub -> { if (sub.speaker() != null && content.characters().get(sub.speaker()).isPresent()) used.add(sub.speaker()); });
            }
        }
        s.areaTriggers().forEach(t -> acts.addAll(t.actions()));
        // Dialogues can start other dialogues; follow them all.
        java.util.Deque<String> queue = new java.util.ArrayDeque<>(dialogues);
        Set<String> seen = new HashSet<>();
        while (!queue.isEmpty()) {
            String id = queue.poll();
            DialogueDef d = id == null ? null : content.dialogue(id);
            if (d == null || !seen.add(id)) continue;
            for (DialogueDef.Node n : d.nodes().values()) {
                used.add(n.speaker());
                List<ActionDef> lineActs = new ArrayList<>(n.actions());
                n.choices().forEach(c -> lineActs.addAll(c.actions()));
                for (ActionDef a : lineActs) if (a.type().equals("start_dialogue")) queue.add(a.arg("id"));
                acts.addAll(lineActs);
            }
        }
        for (int i = 0; i < acts.size(); i++) {
            ActionDef a = acts.get(i);
            if (a.type().startsWith("npc_") || a.type().equals("say")) used.add(a.arg("character"));
            if (a.type().equals("start_dialogue") && !seen.contains(a.arg("id")) && content.dialogue(a.arg("id")) != null) {
                seen.add(a.arg("id"));
                for (DialogueDef.Node n : content.dialogue(a.arg("id")).nodes().values()) {
                    used.add(n.speaker());
                    acts.addAll(n.actions());
                    n.choices().forEach(c -> acts.addAll(c.actions()));
                }
            }
        }
        for (String cid : used) {
            if (cid == null) continue;
            content.characters().get(cid).ifPresent(c -> {
                if (!c.castIn(s.saga())) errors.add("scene " + s.id() + ": " + cid + " is not cast in Saga " + s.saga());
            });
        }
    }

    private static void validateDialogue(StoryContent content, DialogueDef d, List<String> errors) {
        String p = "dialogue " + d.id() + ": ";
        if (!d.nodes().containsKey(d.start())) errors.add(p + "start line missing");
        for (DialogueDef.Node n : d.nodes().values()) {
            character(content, n.speaker(), p + "line " + n.id(), errors);
            if (n.next() != null && !d.nodes().containsKey(n.next())) errors.add(p + "line " + n.id() + " -> missing " + n.next());
            if (n.hasChoices() && n.next() != null) errors.add(p + "line " + n.id() + " has both choices and next");
            for (DialogueDef.Choice c : n.choices()) {
                if (c.next() != null && !d.nodes().containsKey(c.next())) errors.add(p + "choice -> missing " + c.next());
            }
        }
    }

    private static void actions(StoryContent content, SceneDef s, List<ActionDef> actions, String p, List<String> errors) {
        for (ActionDef a : actions) {
            if (!ACTIONS.contains(a.type())) errors.add(p + "unknown action " + a.type());
            if (a.type().equals("start_dialogue") && content.dialogue(a.arg("id")) == null) errors.add(p + "dialogue " + a.arg("id") + " missing");
            if (a.type().equals("start_cinematic") && content.cinematic(a.arg("id")) == null) errors.add(p + "cinematic " + a.arg("id") + " missing");
            if (a.type().startsWith("npc_")) character(content, a.arg("character"), p + a.type(), errors);
            if (a.arg("location") != null) location(s, a.arg("location"), p + a.type(), errors);
            if (a.type().equals("say")) character(content, a.arg("character"), p + "say", errors);
            if (a.type().equals("journal") && content.journal(a.arg("id")) == null) errors.add(p + "journal entry " + a.arg("id") + " missing");
            if (Set.of("teleport_player", "particles", "npc_sleep", "npc_look").contains(a.type()) && a.arg("location") == null) {
                errors.add(p + a.type() + " needs a location");
            }
        }
    }

    private static void character(StoryContent content, String id, String where, List<String> errors) {
        if (id == null || content.characters().get(id).isEmpty()) errors.add(where + ": unknown character " + id);
    }

    private static void location(SceneDef s, String name, String where, List<String> errors) {
        if (name == null || !s.locations().containsKey(name)) errors.add(where + ": unknown location " + name);
    }
}
