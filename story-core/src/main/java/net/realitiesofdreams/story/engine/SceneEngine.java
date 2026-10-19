package net.realitiesofdreams.story.engine;

import net.realitiesofdreams.story.StoryContent;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.scene.SceneDef.CompletionType;
import net.realitiesofdreams.story.scene.SceneDef.StageDef;
import net.realitiesofdreams.story.state.PlayerStoryState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;

/**
 * The scene state machine. It never touches Minecraft: it updates {@link PlayerStoryState}
 * and returns {@link Effect}s for the game layer to perform.
 */
public final class SceneEngine {
    public static final String FOLLOW_FLAG = "npc.follow.";

    private final StoryContent content;

    public SceneEngine(StoryContent content) {
        this.content = content;
    }

    public StoryContent content() {
        return content;
    }

    public SceneDef activeScene(PlayerStoryState state) {
        return state.activeScene() == null ? null : content.scene(state.activeScene());
    }

    public StageDef activeStage(PlayerStoryState state) {
        SceneDef scene = activeScene(state);
        return scene == null || state.stage() == null ? null : scene.stage(state.stage());
    }

    public List<Effect> startScene(PlayerStoryState state, String sceneId) {
        SceneDef scene = content.scene(sceneId);
        if (scene == null) throw new IllegalArgumentException("unknown scene " + sceneId);
        state.setScene(sceneId, null);
        List<Effect> out = new ArrayList<>();
        enterStage(state, scene, scene.stages().get(0), out);
        return out;
    }

    /** Development and tests: enters a stage of the active scene directly, running its on-enter actions. */
    public List<Effect> enterStageForTesting(PlayerStoryState state, String stageId) {
        SceneDef scene = activeScene(state);
        if (scene == null || scene.stage(stageId) == null) throw new IllegalArgumentException("unknown stage " + stageId);
        List<Effect> out = new ArrayList<>();
        enterStage(state, scene, scene.stage(stageId), out);
        return out;
    }

    /** Effects that rebuild the player's view of the story after logging back in. */
    public List<Effect> resume(PlayerStoryState state) {
        List<Effect> out = new ArrayList<>();
        SceneDef scene = activeScene(state);
        StageDef stage = activeStage(state);
        if (scene == null || stage == null) return out;
        for (String flag : state.flags()) {
            if (flag.startsWith(FOLLOW_FLAG)) out.add(new Effect.NpcCommand("follow", flag.substring(FOLLOW_FLAG.length()), null));
        }
        if (stage.completion() == CompletionType.CINEMATIC_END) {
            // A cinematic interrupted by logging out counts as watched.
            out.addAll(handle(state, new StoryEvent.CinematicFinished(stage.completionValue())));
        } else {
            out.add(objective(state, scene, stage));
            // Conversations are not saved mid-line: replay any the stage started that never finished,
            // so logging out halfway through one cannot leave the stage stuck.
            java.util.Set<String> replay = new java.util.LinkedHashSet<>();
            for (ActionDef a : stage.onEnter()) if (a.type().equals("start_dialogue")) replay.add(a.arg("id"));
            if (stage.completion() == CompletionType.DIALOGUE_END
                    && scene.triggers().stream().noneMatch(t -> t.dialogue().equals(stage.completionValue()))) {
                replay.add(stage.completionValue());
            }
            for (String id : replay) if (id != null && !state.hasFlag(dialogueDoneFlag(scene, id))) out.add(new Effect.StartDialogue(id));
            // A character who was cut off mid-conversation will start it again when the player comes near.
            for (SceneDef.TriggerDef t : scene.triggers()) {
                if (state.triggerFired(t.id()) && !state.hasFlag(dialogueDoneFlag(scene, t.dialogue()))) state.unmarkTriggerFired(t.id());
            }
        }
        return out;
    }

    /** Flag recording that a scene's conversation has played to the end (reset with the scene). */
    public static String dialogueDoneFlag(SceneDef scene, String dialogueId) {
        return scene.id().replace("_s", ".s") + ".dialogue_done." + dialogueId;
    }

    public List<Effect> handle(PlayerStoryState state, StoryEvent event) {
        List<Effect> out = new ArrayList<>();
        Deque<StoryEvent> queue = new ArrayDeque<>();
        queue.add(event);
        int guard = 0;
        while (!queue.isEmpty() && guard++ < 64) {
            process(state, queue.poll(), out, queue);
        }
        return out;
    }

    private void process(PlayerStoryState state, StoryEvent event, List<Effect> out, Deque<StoryEvent> queue) {
        SceneDef scene = activeScene(state);
        StageDef stage = activeStage(state);
        if (scene == null || stage == null) return;
        if (event instanceof StoryEvent.DialogueFinished d) state.setFlag(dialogueDoneFlag(scene, d.dialogueId()));

        if (event instanceof StoryEvent.ClueExamined clue) {
            SceneDef.ClueDef def = scene.clues().get(clue.clueId());
            if (def == null || stage.completion() != CompletionType.CLUES || !stageClues(stage).contains(clue.clueId())) return;
            if (!state.addClue(clue.clueId())) return;
            if (def.dialogue() != null) out.add(new Effect.StartDialogue(def.dialogue()));
            out.add(objective(state, scene, stage));
            if (state.cluesFound().containsAll(stageClues(stage))) completeStage(state, scene, stage, out, queue);
            return;
        }
        if (matches(stage, event)) completeStage(state, scene, stage, out, queue);
    }

    private static boolean matches(StageDef stage, StoryEvent event) {
        String v = stage.completionValue();
        return switch (stage.completion()) {
            case CINEMATIC_END -> event instanceof StoryEvent.CinematicFinished e && e.cinematicId().equals(v);
            case DIALOGUE_END -> event instanceof StoryEvent.DialogueFinished e && e.dialogueId().equals(v);
            case REACH -> event instanceof StoryEvent.Reached e && e.location().equals(v);
            case FLAG -> event instanceof StoryEvent.FlagSet e && e.flag().equals(v);
            case CLUES -> false;
            case ITEMS -> event instanceof StoryEvent.ItemsCollected e && e.stage().equals(stage.id());
            case BLOCKS -> event instanceof StoryEvent.BlocksDone e && e.stage().equals(stage.id());
        };
    }

    /** One item an ITEMS stage asks for. */
    public record ItemGoal(String item, int count) {}

    /** "minecraft:compass,minecraft:torch*8" as goals. */
    public static List<ItemGoal> stageItems(StageDef stage) {
        List<ItemGoal> out = new java.util.ArrayList<>();
        if (stage.completionValue() == null) return out;
        for (String part : stage.completionValue().split(",")) {
            String[] p = part.trim().split("\\*");
            out.add(new ItemGoal(p[0], p.length > 1 ? Integer.parseInt(p[1]) : 1));
        }
        return out;
    }

    /** A BLOCKS stage: what each spot must become, and where the spots are (relative to the scene anchor). */
    public record BlockGoal(String mode, List<int[]> spots) {}

    public static BlockGoal stageBlocks(StageDef stage) {
        String v = stage.completionValue() == null ? "" : stage.completionValue();
        int bar = v.indexOf('|');
        List<int[]> spots = new java.util.ArrayList<>();
        if (bar < 0) return new BlockGoal("solid", spots);
        for (String s : v.substring(bar + 1).split(";")) {
            String[] c = s.trim().split(",");
            if (c.length == 3) spots.add(new int[] {Integer.parseInt(c[0].trim()), Integer.parseInt(c[1].trim()), Integer.parseInt(c[2].trim())});
        }
        return new BlockGoal(v.substring(0, bar).trim(), spots);
    }

    /** State value holding how many spots of a BLOCKS stage are done (kept up to date by the game). */
    public static final String BLOCKS_PROGRESS = "blocks.progress";

    /** State value holding how many of an ITEMS stage's goals the player has (kept up to date by the game). */
    public static final String ITEMS_PROGRESS = "items.progress";

    public static List<String> stageClues(StageDef stage) {
        return stage.completionValue() == null ? List.of() : Arrays.asList(stage.completionValue().split(","));
    }

    private void completeStage(PlayerStoryState state, SceneDef scene, StageDef stage, List<Effect> out, Deque<StoryEvent> queue) {
        runActions(state, scene, stage.onComplete(), out, queue);
        if (!scene.id().equals(state.activeScene()) || !stage.id().equals(state.stage())) return; // an action moved the story
        StageDef next = scene.nextStage(stage);
        if (next == null) {
            state.completeScene(scene.id());
            state.setScene(null, null);
            // Companions stay with the player when the story carries straight on into the next scene.
            if (scene.nextScene() == null || content.scene(scene.nextScene()) == null) state.flags().removeIf(f -> f.startsWith(FOLLOW_FLAG));
            out.add(new Effect.ClearObjective());
            out.add(new Effect.SceneCompleted(scene.id(), scene.nextScene()));
        } else {
            enterStage(state, scene, next, out);
            // Fire any queued completion for the new stage (e.g. a flag set on entry).
            for (String f : state.flags()) if (next.completion() == CompletionType.FLAG && f.equals(next.completionValue())) {
                queue.add(new StoryEvent.FlagSet(f));
                break;
            }
        }
    }

    private void enterStage(PlayerStoryState state, SceneDef scene, StageDef stage, List<Effect> out) {
        state.setStage(stage.id());
        if (stage.checkpoint() != null) out.add(new Effect.Checkpoint(stage.checkpoint()));
        if (!stage.objective().isEmpty()) out.add(objective(state, scene, stage));
        else out.add(new Effect.ClearObjective());
        Deque<StoryEvent> ignored = new ArrayDeque<>();
        runActions(state, scene, stage.onEnter(), out, ignored);
    }

    public Effect objective(PlayerStoryState state, SceneDef scene, StageDef stage) {
        String progress = null;
        if (stage.completion() == CompletionType.CLUES) {
            List<String> clues = stageClues(stage);
            long found = clues.stream().filter(state.cluesFound()::contains).count();
            progress = found + "/" + clues.size();
        } else if (stage.completion() == CompletionType.ITEMS) {
            progress = state.value(ITEMS_PROGRESS) + "/" + stageItems(stage).size();
        } else if (stage.completion() == CompletionType.BLOCKS) {
            progress = state.value(BLOCKS_PROGRESS) + "/" + stageBlocks(stage).spots().size();
        }
        return new Effect.ShowObjective(scene.label(), scene.title(), stage.objective(), scene.location(stage.target()), progress);
    }

    /** Runs area triggers for every location the player is standing in. Call it a few times a second. */
    public List<Effect> checkAreas(PlayerStoryState state, Predicate<SceneDef.Location> inside) {
        List<Effect> out = new ArrayList<>();
        SceneDef scene = activeScene(state);
        if (scene == null) return out;
        for (SceneDef.AreaTrigger t : scene.areaTriggers()) {
            if (!scene.id().equals(state.activeScene())) break; // an earlier trigger finished the scene
            String key = "area:" + t.id();
            if (state.triggerFired(key)) continue;
            if (!t.stages().isEmpty() && !t.stages().contains(state.stage())) continue;
            if (!t.requires().stream().allMatch(state::hasFlag) || t.forbids().stream().anyMatch(state::hasFlag)) continue;
            if (!inside.test(scene.location(t.location()))) continue;
            state.markTriggerFired(key);
            out.addAll(applyActions(state, t.actions()));
        }
        return out;
    }

    private void runActions(PlayerStoryState state, SceneDef scene, List<ActionDef> actions, List<Effect> out, Deque<StoryEvent> queue) {
        for (ActionDef a : actions) apply(state, scene, a, out, queue);
    }

    /** Applies dialogue actions (fired by lines and choices) for the player's active scene. */
    public List<Effect> applyActions(PlayerStoryState state, List<ActionDef> actions) {
        List<Effect> out = new ArrayList<>();
        Deque<StoryEvent> queue = new ArrayDeque<>();
        SceneDef scene = activeScene(state);
        for (ActionDef a : actions) apply(state, scene, a, out, queue);
        while (!queue.isEmpty()) out.addAll(handle(state, queue.poll()));
        return out;
    }

    private void apply(PlayerStoryState state, SceneDef scene, ActionDef a, List<Effect> out, Deque<StoryEvent> queue) {
        switch (a.type()) {
            case "set_flag" -> {
                if (state.setFlag(a.arg("flag"))) queue.add(new StoryEvent.FlagSet(a.arg("flag")));
            }
            case "clear_flag" -> state.clearFlag(a.arg("flag"));
            case "start_dialogue" -> out.add(new Effect.StartDialogue(a.arg("id")));
            case "start_cinematic" -> out.add(new Effect.StartCinematic(a.arg("id")));
            case "npc_follow" -> {
                state.setFlag(FOLLOW_FLAG + a.arg("character"));
                out.add(new Effect.NpcCommand("follow", a.arg("character"), null));
            }
            case "npc_stay" -> {
                state.clearFlag(FOLLOW_FLAG + a.arg("character"));
                out.add(new Effect.NpcCommand("stay", a.arg("character"), a.arg("location")));
            }
            case "npc_move" -> out.add(new Effect.NpcCommand("move", a.arg("character"), a.arg("location")));
            case "npc_spawn" -> out.add(new Effect.NpcCommand("spawn", a.arg("character"), a.arg("location")));
            case "npc_despawn" -> out.add(new Effect.NpcCommand("despawn", a.arg("character"), null));
            case "npc_sleep" -> out.add(new Effect.NpcCommand("sleep", a.arg("character"), a.arg("location")));
            case "npc_wake" -> out.add(new Effect.NpcCommand("wake", a.arg("character"), null));
            case "npc_approach" -> out.add(new Effect.NpcCommand("approach", a.arg("character"), null));
            case "npc_look" -> out.add(new Effect.NpcCommand("look", a.arg("character"), a.arg("location")));
            case "hint" -> out.add(new Effect.Hint(a.arg("text"), Integer.parseInt(a.arg("ticks", "120"))));
            case "shake" -> out.add(new Effect.Shake(Float.parseFloat(a.arg("intensity", "0.1")), Integer.parseInt(a.arg("ticks", "20"))));
            case "say" -> out.add(new Effect.Say(a.arg("character"), a.arg("text")));
            case "teleport_player" -> out.add(new Effect.TeleportPlayer(a.arg("location"), Float.parseFloat(a.arg("yaw", "0"))));
            case "journal" -> {
                if (state.addJournal(a.arg("id"))) out.add(new Effect.Journal(a.arg("id")));
            }
            case "set_value" -> state.setValue(a.arg("key"), Integer.parseInt(a.arg("value", "0")));
            case "add_value" -> state.setValue(a.arg("key"), state.value(a.arg("key")) + Integer.parseInt(a.arg("amount", "1")));
            case "particles" -> out.add(new Effect.Particles(a.arg("particle"), a.arg("location"),
                    Integer.parseInt(a.arg("count", "12")), Double.parseDouble(a.arg("spread", "1"))));
            case "title" -> out.add(new Effect.ShowTitle(a.arg("title", ""), a.arg("subtitle", "")));
            case "give_item" -> out.add(new Effect.GiveItem(a.arg("item"), Integer.parseInt(a.arg("count", "1")),
                    a.arg("name"), a.arg("lore") == null ? List.of() : List.of(a.arg("lore").split("\\|"))));
            case "hold_item" -> out.add(new Effect.HoldItem(a.arg("item")));
            case "sound" -> out.add(new Effect.PlaySound(a.arg("sound"), Float.parseFloat(a.arg("volume", "1")),
                    Float.parseFloat(a.arg("pitch", "1"))));
            case "world_change" -> out.add(new Effect.WorldChange(a.arg("id")));
            case "message" -> out.add(new Effect.Message(a.arg("text")));
            default -> throw new IllegalStateException("unknown action type " + a.type() + " in scene " + (scene == null ? "?" : scene.id()));
        }
    }
}
