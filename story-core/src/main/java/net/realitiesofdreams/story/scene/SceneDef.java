package net.realitiesofdreams.story.scene;

import com.google.gson.JsonObject;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.json.Json;
import net.realitiesofdreams.story.json.StoryFormatException;

import java.util.List;
import java.util.Map;

/** A playable story scene: locations, cast, staged objectives and natural dialogue triggers. */
public record SceneDef(String id, String title, String universe, int saga, int sceneNumber, String canonStatus,
                       String dimension, int[] anchor, Location arrival, float arrivalYaw,
                       Map<String, Location> locations, List<NpcPlacement> npcs, List<StageDef> stages,
                       List<TriggerDef> triggers, List<ChatterDef> chatter, Map<String, ClueDef> clues,
                       String nextScene, String site, List<AreaTrigger> areaTriggers) {

    /** A point relative to the scene anchor. */
    public record Location(double x, double y, double z, double radius) {
        static Location fromJson(String name, JsonObject o) {
            double[] p = Json.vec3(o, "pos", "location " + name);
            return new Location(p[0], p[1], p[2], Json.number(o, "radius", 4));
        }

        public double distanceTo(double ox, double oy, double oz) {
            double dx = ox - x, dy = oy - y, dz = oz - z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    /**
     * Where a character stands, and from which stage they are present. activity: "idle" (potters
     * near home), "work" (busy with the item in hand), "patrol" (walks the route of locations) or
     * "routine" (goes between the route's stops, staying a few minutes at each).
     */
    public record NpcPlacement(String character, String location, String fromStage, String untilStage, float yaw,
                               String activity, String held, List<String> route) {
        static NpcPlacement fromJson(JsonObject o) {
            return new NpcPlacement(Json.req(o, "character", "npc"), Json.req(o, "location", "npc"),
                    Json.str(o, "from_stage", null), Json.str(o, "until_stage", null), (float) Json.number(o, "yaw", 0),
                    Json.str(o, "activity", "idle"), Json.str(o, "held", null), Json.strings(o, "route"));
        }
    }

    /** Actions that run once when the player walks into a location during the listed stages. */
    public record AreaTrigger(String id, String location, List<String> stages, List<String> requires, List<String> forbids,
                              List<ActionDef> actions) {
        static AreaTrigger fromJson(JsonObject o) {
            String id = Json.req(o, "id", "area trigger");
            return new AreaTrigger(id, Json.req(o, "location", "area trigger " + id), Json.strings(o, "stages"),
                    Json.strings(o, "requires"), Json.strings(o, "forbids"), ActionDef.listFrom(o, "actions"));
        }
    }

    public enum CompletionType { CINEMATIC_END, DIALOGUE_END, REACH, CLUES, FLAG }

    public record StageDef(String id, String objective, String target, List<ActionDef> onEnter,
                           CompletionType completion, String completionValue, List<ActionDef> onComplete,
                           String next, String checkpoint, boolean chatter) {
        static StageDef fromJson(JsonObject o) {
            String id = Json.req(o, "id", "stage");
            JsonObject c = Json.obj(o, "complete");
            String type = Json.req(c, "type", "stage " + id + " complete");
            CompletionType ct;
            try {
                ct = CompletionType.valueOf(type.toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new StoryFormatException("stage " + id + ": unknown completion type " + type);
            }
            return new StageDef(id, Json.str(o, "objective", ""), Json.str(o, "target", null),
                    ActionDef.listFrom(o, "on_enter"), ct, Json.str(c, "value", null),
                    ActionDef.listFrom(o, "on_complete"), Json.str(o, "next", null),
                    Json.str(o, "checkpoint", null), Json.bool(o, "chatter", false));
        }
    }

    /** Dialogue that starts by itself when the character is near the player. */
    public record TriggerDef(String id, String character, String dialogue, double radius, double talkRadius,
                             boolean approach, boolean once, List<String> stages, List<String> requires,
                             List<String> forbids, int priority, boolean ambient) {
        static TriggerDef fromJson(JsonObject o) {
            String id = Json.req(o, "id", "trigger");
            return new TriggerDef(id, Json.req(o, "character", id), Json.req(o, "dialogue", id),
                    Json.number(o, "radius", 8), Json.number(o, "talk_radius", 3.5), Json.bool(o, "approach", true),
                    Json.bool(o, "once", true), Json.strings(o, "stages"), Json.strings(o, "requires"),
                    Json.strings(o, "forbids"), Json.integer(o, "priority", 0), Json.bool(o, "ambient", false));
        }
    }

    /** A line a companion may say on their own while travelling, exploring or fighting. */
    public record ChatterDef(String id, String character, String text, List<String> stages, String context) {
        static ChatterDef fromJson(JsonObject o) {
            String id = Json.req(o, "id", "chatter");
            return new ChatterDef(id, Json.req(o, "character", id), Json.req(o, "text", id), Json.strings(o, "stages"),
                    Json.str(o, "context", "moving"));
        }
    }

    /** Something the player can examine (right-click). */
    public record ClueDef(String id, String name, double[] pos, String block, String dialogue) {
        static ClueDef fromJson(String id, JsonObject o) {
            return new ClueDef(id, Json.req(o, "name", "clue " + id), Json.vec3(o, "pos", "clue " + id),
                    Json.str(o, "block", "minecraft:lodestone"), Json.str(o, "dialogue", null));
        }
    }

    public static SceneDef fromJson(JsonObject o) {
        String id = Json.req(o, "id", "scene");
        JsonObject a = Json.obj(o, "arrival");
        double[] anchor = Json.vec3(o, "anchor", id);
        return new SceneDef(id, Json.req(o, "title", id), Json.req(o, "universe", id), Json.integer(o, "saga", 0),
                Json.integer(o, "scene", 0), Json.str(o, "canon_status", "provisional"),
                Json.str(o, "dimension", "minecraft:overworld"),
                new int[] {(int) anchor[0], (int) anchor[1], (int) anchor[2]},
                Location.fromJson("arrival", a), (float) Json.number(a, "yaw", 0),
                Json.map(o, "locations", Location::fromJson), Json.list(o, "npcs", NpcPlacement::fromJson),
                Json.list(o, "stages", StageDef::fromJson), Json.list(o, "triggers", TriggerDef::fromJson),
                Json.list(o, "chatter", ChatterDef::fromJson), Json.map(o, "clues", ClueDef::fromJson),
                Json.str(o, "next_scene", null), Json.str(o, "site", id), Json.list(o, "area_triggers", AreaTrigger::fromJson));
    }

    /** Placements of a character in this scene (a character may stand in different places per stage). */
    public List<NpcPlacement> placementsFor(String character) {
        return npcs.stream().filter(n -> n.character().equals(character)).toList();
    }

    /** Whether the placement is present during the given stage. */
    public boolean presentDuring(NpcPlacement n, String stageId) {
        int stage = stageIndex(stageId);
        int from = n.fromStage() == null ? 0 : stageIndex(n.fromStage());
        int until = n.untilStage() == null ? Integer.MAX_VALUE : stageIndex(n.untilStage());
        return stage >= from && stage < until;
    }

    public StageDef stage(String stageId) {
        for (StageDef s : stages) if (s.id().equals(stageId)) return s;
        return null;
    }

    public int stageIndex(String stageId) {
        for (int i = 0; i < stages.size(); i++) if (stages.get(i).id().equals(stageId)) return i;
        return -1;
    }

    public StageDef nextStage(StageDef s) {
        if (s.next() != null) return stage(s.next());
        int i = stageIndex(s.id());
        return i + 1 < stages.size() ? stages.get(i + 1) : null;
    }

    public Location location(String name) {
        return name == null ? null : locations.get(name);
    }

    /** "MC-001 · Scene 001" style label. */
    public String label() {
        return String.format(java.util.Locale.ROOT, "%s · Scene %03d", universe, sceneNumber);
    }
}
