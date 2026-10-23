package net.realitiesofdreams.story.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Small, strict helpers for reading story content files. */
public final class Json {
    private Json() {}

    public static String str(JsonObject o, String key, String def) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? def : e.getAsString();
    }

    public static String req(JsonObject o, String key, String where) {
        JsonElement e = o.get(key);
        if (e == null || e.isJsonNull()) throw new StoryFormatException(where + ": missing \"" + key + "\"");
        return e.getAsString();
    }

    public static int integer(JsonObject o, String key, int def) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? def : e.getAsInt();
    }

    public static double number(JsonObject o, String key, double def) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? def : e.getAsDouble();
    }

    public static boolean bool(JsonObject o, String key, boolean def) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? def : e.getAsBoolean();
    }

    public static JsonObject obj(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonObject() ? e.getAsJsonObject() : new JsonObject();
    }

    public static JsonArray arr(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonArray() ? e.getAsJsonArray() : new JsonArray();
    }

    public static List<String> strings(JsonObject o, String key) {
        List<String> out = new ArrayList<>();
        for (JsonElement e : arr(o, key)) out.add(e.getAsString());
        return Collections.unmodifiableList(out);
    }

    public static <T> List<T> list(JsonObject o, String key, Function<JsonObject, T> reader) {
        List<T> out = new ArrayList<>();
        for (JsonElement e : arr(o, key)) out.add(reader.apply(e.getAsJsonObject()));
        return Collections.unmodifiableList(out);
    }

    public static <T> Map<String, T> map(JsonObject o, String key, java.util.function.BiFunction<String, JsonObject, T> reader) {
        Map<String, T> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : obj(o, key).entrySet()) {
            out.put(e.getKey(), reader.apply(e.getKey(), e.getValue().getAsJsonObject()));
        }
        return Collections.unmodifiableMap(out);
    }

    public static double[] vec3(JsonObject o, String key, String where) {
        JsonArray a = arr(o, key);
        if (a.size() != 3) throw new StoryFormatException(where + ": \"" + key + "\" must be [x, y, z]");
        return new double[] {a.get(0).getAsDouble(), a.get(1).getAsDouble(), a.get(2).getAsDouble()};
    }
}
