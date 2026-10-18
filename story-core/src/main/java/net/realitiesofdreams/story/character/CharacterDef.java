package net.realitiesofdreams.story.character;

import com.google.gson.JsonObject;
import net.realitiesofdreams.story.json.Json;

import java.util.Set;
import java.util.TreeSet;

/** One entry of the character registry (see docs/CHARACTER_REGISTRY.md). */
public record CharacterDef(String id, String name, String universe, String group, String faction, String role,
                           String notes, String entity, String texture, String model, boolean generated,
                           String speakerName, Set<Integer> sagas) {

    public static final String NPC_ENTITY = "realitiesofdreams:story_npc";

    public static CharacterDef fromJson(JsonObject o) {
        String id = Json.req(o, "id", "character");
        return new CharacterDef(id, Json.req(o, "name", id), Json.str(o, "universe", null), Json.str(o, "group", ""),
                Json.str(o, "faction", ""), Json.str(o, "role", ""), Json.str(o, "notes", ""),
                Json.str(o, "entity", NPC_ENTITY), Json.str(o, "texture", null), Json.str(o, "model", null),
                Json.bool(o, "generated", false), Json.str(o, "speaker_name", Json.req(o, "name", id)), sagas(o));
    }

    /** The sagas this character is cast in; a scene may only use characters cast in its saga. */
    private static Set<Integer> sagas(JsonObject o) {
        Set<Integer> out = new TreeSet<>();
        if (o.has("sagas")) o.getAsJsonArray("sagas").forEach(e -> out.add(e.getAsInt()));
        return Set.copyOf(out);
    }

    public boolean castIn(int saga) {
        return sagas.contains(saga);
    }

    public boolean usesPlayerModel() {
        return NPC_ENTITY.equals(entity);
    }

    public boolean slim() {
        return "slim".equals(model);
    }
}
