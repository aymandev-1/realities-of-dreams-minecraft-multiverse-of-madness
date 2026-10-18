package net.realitiesofdreams.story;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.character.CharacterRegistry;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.dialogue.DialogueDef;
import net.realitiesofdreams.story.json.Json;
import net.realitiesofdreams.story.json.StoryFormatException;
import net.realitiesofdreams.story.scene.SceneDef;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * All story data: the character registry plus scene bundles. A bundle file holds one scene and
 * the dialogues and cinematics it uses, listed in {@code rod-story/index.json}.
 */
public final class StoryContent {
    public static final String ROOT = "/rod-story/";

    private final CharacterRegistry characters;
    private final Map<String, SceneDef> scenes = new LinkedHashMap<>();
    private final Map<String, DialogueDef> dialogues = new LinkedHashMap<>();
    private final Map<String, CinematicDef> cinematics = new LinkedHashMap<>();

    public interface Source {
        Reader open(String path) throws IOException;
    }

    private StoryContent(CharacterRegistry characters) {
        this.characters = characters;
    }

    /** Loads from the classpath (works in the mod jar and in tests). */
    public static StoryContent loadFromClasspath(Class<?> anchor) {
        return load(path -> {
            InputStream in = anchor.getResourceAsStream(ROOT + path);
            if (in == null) throw new IOException("missing story file " + ROOT + path);
            return new InputStreamReader(in, StandardCharsets.UTF_8);
        });
    }

    public static StoryContent load(Source source) {
        try {
            StoryContent c;
            try (Reader r = source.open("characters.json")) {
                c = new StoryContent(CharacterRegistry.load(r));
            }
            JsonObject index;
            try (Reader r = source.open("index.json")) {
                index = JsonParser.parseReader(r).getAsJsonObject();
            }
            for (String bundle : Json.strings(index, "scenes")) {
                try (Reader r = source.open("scenes/" + bundle)) {
                    c.addBundle(bundle, JsonParser.parseReader(r).getAsJsonObject());
                }
            }
            return c;
        } catch (IOException e) {
            throw new StoryFormatException(e.getMessage());
        }
    }

    private void addBundle(String file, JsonObject o) {
        SceneDef scene = SceneDef.fromJson(Json.obj(o, "scene"));
        if (scenes.put(scene.id(), scene) != null) throw new StoryFormatException(file + ": duplicate scene " + scene.id());
        for (Entry<String, DialogueDef> e : Json.map(o, "dialogues", DialogueDef::fromJson).entrySet()) {
            if (dialogues.put(e.getKey(), e.getValue()) != null) throw new StoryFormatException(file + ": duplicate dialogue " + e.getKey());
        }
        for (Entry<String, CinematicDef> e : Json.map(o, "cinematics", CinematicDef::fromJson).entrySet()) {
            if (cinematics.put(e.getKey(), e.getValue()) != null) throw new StoryFormatException(file + ": duplicate cinematic " + e.getKey());
        }
    }

    public CharacterRegistry characters() { return characters; }
    public SceneDef scene(String id) { return scenes.get(id); }
    public DialogueDef dialogue(String id) { return dialogues.get(id); }
    public CinematicDef cinematic(String id) { return cinematics.get(id); }
    public Collection<SceneDef> scenes() { return scenes.values(); }
    public Map<String, DialogueDef> dialogues() { return dialogues; }
    public Map<String, CinematicDef> cinematics() { return cinematics; }
}
