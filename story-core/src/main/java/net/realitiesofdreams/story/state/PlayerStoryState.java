package net.realitiesofdreams.story.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.json.Json;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
    /** Numbers the story keeps, e.g. "rel.elara" for Elara's relationship with Steve. */
    private final Map<String, Integer> values = new LinkedHashMap<>();
    /** Journal entries Steve has written, in order. */
    private final Set<String> journal = new LinkedHashSet<>();
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
    public void unmarkTriggerFired(String id) { firedTriggers.remove(id); }

    public Set<String> spokenChatter() { return spokenChatter; }

    public Map<String, Integer> values() { return values; }
    public int value(String key) { return values.getOrDefault(key, 0); }
    public boolean hasValue(String key) { return values.containsKey(key); }
    public void setValue(String key, int v) { values.put(key, v); }

    public Set<String> journal() { return journal; }
    public boolean addJournal(String id) { return journal.add(id); }

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
        JsonObject v = new JsonObject();
        values.forEach(v::addProperty);
        o.add("values", v);
        o.add("journal", array(journal));
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
        if (o.has("values")) o.getAsJsonObject("values").entrySet().forEach(e -> s.values.put(e.getKey(), e.getValue().getAsInt()));
        s.journal.addAll(Json.strings(o, "journal"));
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
