package net.realitiesofdreams.story.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.realitiesofdreams.story.json.Json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A story action, e.g. {"type": "start_cinematic", "id": "..."}. Every other key is kept as a
 * string argument.
 */
public record ActionDef(String type, Map<String, String> args) {

    public static ActionDef fromJson(JsonObject o) {
        Map<String, String> args = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : o.entrySet()) {
            if (e.getKey().equals("type")) continue;
            JsonElement v = e.getValue();
            args.put(e.getKey(), v.isJsonPrimitive() ? v.getAsString() : v.toString());
        }
        return new ActionDef(Json.req(o, "type", "action"), Collections.unmodifiableMap(args));
    }

    public static List<ActionDef> listFrom(JsonObject o, String key) {
        return Json.list(o, key, ActionDef::fromJson);
    }

    public String arg(String key) {
        return args.get(key);
    }

    public String arg(String key, String def) {
        return args.getOrDefault(key, def);
    }

    public static ActionDef of(String type, String... kv) {
        Map<String, String> args = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) args.put(kv[i], kv[i + 1]);
        return new ActionDef(type, Collections.unmodifiableMap(args));
    }
}
