package net.realitiesofdreams.story.saga;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.json.StoryFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** The saga and scene list shown by Chapter / Saga Selection (rod-story/sagas.json). */
public record SagaIndex(List<Saga> sagas) {
    public record Saga(int number, String title, String universe, List<SceneEntry> scenes) {}

    public record SceneEntry(int number, String id, String title) {}

    public static SagaIndex loadFromClasspath(Class<?> anchor) {
        try (InputStream in = anchor.getResourceAsStream("/rod-story/sagas.json")) {
            if (in == null) throw new StoryFormatException("rod-story/sagas.json missing");
            return parse(JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject());
        } catch (IOException e) {
            throw new StoryFormatException("rod-story/sagas.json unreadable: " + e.getMessage());
        }
    }

    public static SagaIndex parse(JsonObject root) {
        List<Saga> sagas = new ArrayList<>();
        for (JsonElement e : root.getAsJsonArray("sagas")) {
            JsonObject s = e.getAsJsonObject();
            List<SceneEntry> scenes = new ArrayList<>();
            for (JsonElement se : s.getAsJsonArray("scenes")) {
                JsonObject o = se.getAsJsonObject();
                scenes.add(new SceneEntry(o.get("number").getAsInt(), o.get("id").getAsString(), o.get("title").getAsString()));
            }
            sagas.add(new Saga(s.get("number").getAsInt(), s.get("title").getAsString(), s.get("universe").getAsString(), List.copyOf(scenes)));
        }
        return new SagaIndex(List.copyOf(sagas));
    }
}
