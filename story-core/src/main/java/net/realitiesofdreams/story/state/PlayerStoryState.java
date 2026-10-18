package net.realitiesofdreams.story.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.json.Json;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/** Everything the story remembers about one player. Saved with the player. */
public final class PlayerStoryState {
    public static final int FORMAT = 1;

    private String activeScene;
    private String stage;
    private final Set<String> flags = new LinkedHashSet<>();
    private final Set<String> cluesFound = new LinkedHashSet<>();
    private final Set<String> completedScenes = new LinkedHashSet<>();
    private final Set<String> firedTriggers = new LinkedHashSet<>();
    private final Set<String> spokenChatter = new LinkedHashSet<>();
    private boolean introOffered;

    public String activeScene() { return activeScene; }
    public String stage() { return stage; }
    public boolean introOffered() { return introOffered; }
    public void setIntroOffered(boolean v) { introOffered = v; }

    public void setScene(String scene, String stage) {
        this.activeScene = scene;
        this.stage = stage;
    }

    public void setStage(String stage) { this.stage = stage; }

    public boolean hasFlag(String f) { return flags.contains(f); }
    public boolean setFlag(String f) { return flags.add(f); }
    public boolean clearFlag(String f) { return flags.remove(f); }
    public Set<String> flags() { return flags; }

    public boolean addClue(String id) { return cluesFound.add(id); }
    public Set<String> cluesFound() { return cluesFound; }

    public void completeScene(String id) { completedScenes.add(id); }
    public boolean isSceneComplete(String id) { return completedScenes.contains(id); }
    public Set<String> completedScenes() { return completedScenes; }

    public boolean triggerFired(String id) { return firedTriggers.contains(id); }
    public void markTriggerFired(String id) { firedTriggers.add(id); }

    public Set<String> spokenChatter() { return spokenChatter; }

    /** Clears progress for one scene so it can be replayed. */
    public void resetScene(String sceneId, Collection<String> sceneFlagsPrefix) {
        if (sceneId.equals(activeScene)) {
            activeScene = null;
            stage = null;
        }
        completedScenes.remove(sceneId);
        cluesFound.clear();
        firedTriggers.clear();
        spokenChatter.clear();
        for (String prefix : sceneFlagsPrefix) flags.removeIf(f -> f.startsWith(prefix));
    }

    public String toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("format", FORMAT);
        if (activeScene != null) o.addProperty("scene", activeScene);
        if (stage != null) o.addProperty("stage", stage);
        o.addProperty("intro_offered", introOffered);
        o.add("flags", array(flags));
        o.add("clues", array(cluesFound));
        o.add("completed", array(completedScenes));
        o.add("fired", array(firedTriggers));
        o.add("chatter", array(spokenChatter));
        return o.toString();
    }

    public static PlayerStoryState fromJson(String json) {
        PlayerStoryState s = new PlayerStoryState();
        if (json == null || json.isBlank()) return s;
        JsonObject o = JsonParser.parseString(json).getAsJsonObject();
        s.activeScene = Json.str(o, "scene", null);
        s.stage = Json.str(o, "stage", null);
        s.introOffered = Json.bool(o, "intro_offered", false);
        s.flags.addAll(Json.strings(o, "flags"));
        s.cluesFound.addAll(Json.strings(o, "clues"));
        s.completedScenes.addAll(Json.strings(o, "completed"));
        s.firedTriggers.addAll(Json.strings(o, "fired"));
        s.spokenChatter.addAll(Json.strings(o, "chatter"));
        return s;
    }

    public PlayerStoryState copy() {
        return fromJson(toJson());
    }

    private static JsonArray array(Collection<String> values) {
        JsonArray a = new JsonArray();
        for (String v : values) a.add(v);
        return a;
    }

}
