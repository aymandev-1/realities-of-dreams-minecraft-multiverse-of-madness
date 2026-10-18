package net.realitiesofdreams.story.character;

import com.google.gson.JsonObject;
import net.realitiesofdreams.story.json.Json;

/** One entry of the character registry (see docs/CHARACTER_REGISTRY.md). */
public record CharacterDef(String id, String name, String universe, String group, String faction, String role,
                           String notes, String entity, String texture, String model, boolean generated) {

    public static final String NPC_ENTITY = "realitiesofdreams:story_npc";

    public static CharacterDef fromJson(JsonObject o) {
        String id = Json.req(o, "id", "character");
        return new CharacterDef(id, Json.req(o, "name", id), Json.str(o, "universe", null), Json.str(o, "group", ""),
                Json.str(o, "faction", ""), Json.str(o, "role", ""), Json.str(o, "notes", ""),
                Json.str(o, "entity", NPC_ENTITY), Json.str(o, "texture", null), Json.str(o, "model", null),
                Json.bool(o, "generated", false));
    }

    public boolean usesPlayerModel() {
        return NPC_ENTITY.equals(entity);
    }

    public boolean slim() {
        return "slim".equals(model);
    }
}
